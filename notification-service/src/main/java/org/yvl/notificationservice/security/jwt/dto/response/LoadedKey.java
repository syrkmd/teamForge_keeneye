package org.yvl.notificationservice.security.jwt.dto.response;

import java.security.PublicKey;

public record LoadedKey(PublicKey publicKey, String kid) {}