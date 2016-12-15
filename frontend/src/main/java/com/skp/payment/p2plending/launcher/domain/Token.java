package com.skp.payment.p2plending.launcher.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skplanet.jose.Jose;
import com.skplanet.jose.JoseBuilders;
import com.skplanet.jose.JoseHeader;
import com.skplanet.jose.jwa.Jwa;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

@Slf4j
public class Token implements Serializable {
    private static final long serialVersionUID = 8275342678338386215L;

    @JsonIgnore
    private static final Long DefaultPeriodSeconds = 60 * 60 * 2L;

    @Getter @Setter private String iss = "SKP-P2P-LENDING";
    @Getter @Setter private String aud = "SKP-P2P-LENDING";
    @Getter @Setter private String typ;
    @Getter @Setter private Long iat;
    @Getter @Setter private Long exp;

    @JsonCreator
    public Token(@JsonProperty("iss") String iss,
                 @JsonProperty("aud") String aud,
                 @JsonProperty("typ") String typ,
                 @JsonProperty("iat") Long iat,
                 @JsonProperty("exp") Long exp
    ) {
        this.iss = iss;
        this.aud = aud;
        this.typ = typ;
        this.iat = iat;
        this.exp = exp;
    }

    public Token() {
        setIat(Calendar.getInstance().getTimeInMillis() / 1000);
        setExp(Calendar.getInstance().getTimeInMillis() / 1000 + DefaultPeriodSeconds);
    }

    public String toString(String symmetricKey) throws IOException {
        return new Jose().configuration(
                JoseBuilders.JsonSignatureCompactSerializationBuilder()
                        .header(new JoseHeader(Jwa.HS256, iss))
                        .payload(new ObjectMapper().writeValueAsString(this))
                        .key(symmetricKey.getBytes())
        ).serialization();
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    public boolean isValidInTime() {
        return DateTime.now().isBefore(exp * 1000);
    }

    public static Token decrypt(String token, String key) {
        try {
            return new ObjectMapper().readValue(
                    new Jose().configuration(
                            JoseBuilders.compactDeserializationBuilder()
                                    .serializedSource(token)
                                    .key(key)
                    ).deserialization(),
                    Token.class
            );
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Token deserialize failed...");
        }

        return null;
    }
}
