use futures_util::StreamExt;
use matrix_sdk::encryption::verification::{
    SasState as SdkSasState, SasVerification, Verification, VerificationRequest,
    VerificationRequestState,
};
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(tag = "phase")]
pub enum VerifEvent {
    Requested { flow_id: String },
    Ready,
    SasStarted,
    KeysExchanged {
        emojis: Vec<EmojiEntry>,
        other_user: String,
        other_device: String,
    },
    Confirmed,
    Done,
    Cancelled { reason: String },
    Error { message: String },
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct EmojiEntry {
    pub symbol: String,
    pub description: String,
}

pub async fn drive_verification_request(
    request: VerificationRequest,
    we_start_sas: bool,
) -> impl futures_util::Stream<Item = VerifEvent> {
    let flow_id = request.flow_id().to_owned();
    async_stream::stream! {
        yield VerifEvent::Requested { flow_id };

        let mut req_changes = request.changes();
        let mut sas_opt: Option<SasVerification> = None;

        loop {
            let Some(state) = req_changes.next().await else {
                yield VerifEvent::Error { message: "Request stream ended".into() };
                return;
            };

            match state {
                VerificationRequestState::Ready { .. } => {
                    yield VerifEvent::Ready;
                    if we_start_sas {
                        match request.start_sas().await {
                            Ok(Some(sas)) => {
                                sas_opt = Some(sas);
                                break;
                            }
                            Ok(None) => {
                                yield VerifEvent::Error {
                                    message: "start_sas returned None".into(),
                                };
                                return;
                            }
                            Err(e) => {
                                yield VerifEvent::Error {
                                    message: format!("start_sas failed: {e}"),
                                };
                                return;
                            }
                        }
                    }
                }
                VerificationRequestState::Transitioned { verification } => {
                    match verification {
                        Verification::SasV1(sas) => {
                            sas_opt = Some(sas);
                            break;
                        }
                        _ => {
                            yield VerifEvent::Error {
                                message: "Non-SAS verification method".into(),
                            };
                            return;
                        }
                    }
                }
                VerificationRequestState::Cancelled(info) => {
                    yield VerifEvent::Cancelled {
                        reason: info.reason().to_owned(),
                    };
                    return;
                }
                VerificationRequestState::Done => {
                    yield VerifEvent::Done;
                    return;
                }
                _ => {}
            }
        }

        let sas = match sas_opt {
            Some(s) => s,
            None => {
                yield VerifEvent::Error {
                    message: "No SAS object".into(),
                };
                return;
            }
        };

        yield VerifEvent::SasStarted;

        let mut sas_changes = sas.changes();
        while let Some(state) = sas_changes.next().await {
            match state {
                SdkSasState::KeysExchanged { emojis, .. } => {
                    let emoji_entries = emojis
                        .map(|e| {
                            e.emojis
                                .iter()
                                .map(|emoji| EmojiEntry {
                                    symbol: emoji.symbol.to_string(),
                                    description: emoji.description.to_string(),
                                })
                                .collect()
                        })
                        .unwrap_or_default();

                    yield VerifEvent::KeysExchanged {
                        emojis: emoji_entries,
                        other_user: sas.other_user_id().to_string(),
                        other_device: sas.other_device().device_id().to_string(),
                    };
                }
                SdkSasState::Confirmed => {
                    yield VerifEvent::Confirmed;
                }
                SdkSasState::Done { .. } => {
                    yield VerifEvent::Done;
                    return;
                }
                SdkSasState::Cancelled(info) => {
                    yield VerifEvent::Cancelled {
                        reason: info.reason().to_owned(),
                    };
                    return;
                }
                _ => {}
            }
        }
    }
}
