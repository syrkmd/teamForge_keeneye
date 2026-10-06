package org.yvl.teamforge.security.jwt.dto.response;

import java.security.PublicKey;

public record LoadedKey(PublicKey publicKey, String kid) {}