package org.yvl.jwtkeycommon.dto.response;

import java.security.PublicKey;

public record LoadedKey(PublicKey publicKey, String kid) {}