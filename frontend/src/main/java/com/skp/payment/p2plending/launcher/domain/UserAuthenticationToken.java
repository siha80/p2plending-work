package com.skp.payment.p2plending.launcher.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skplanet.jose.Jose;
import com.skplanet.jose.JoseBuilders;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Calendar;

@Slf4j
public class UserAuthenticationToken extends Token {
    private static final long serialVersionUID = 3869215453409573047L;

    @JsonIgnore
    private static final Long DefaultPeriodSeconds = 60 * 60 * 2L;

    @Getter @Setter private String userSerialNumber;

    @JsonCreator
    public UserAuthenticationToken(@JsonProperty("iss") String iss,
                                   @JsonProperty("aud") String aud,
                                   @JsonProperty("typ") String typ,
                                   @JsonProperty("iat") Long iat,
                                   @JsonProperty("exp") Long exp,
                                   @JsonProperty("userSerialNumber") String userSerialNumber
    ) {
        setIss(iss);
        setAud(aud);
        setTyp(typ);
        setIat(iat);
        setExp(exp);
        this.userSerialNumber = userSerialNumber;
    }

    public UserAuthenticationToken(String userSerialNumber) {
        setUserSerialNumber(userSerialNumber);
        setIat(Calendar.getInstance().getTimeInMillis() / 1000);
        setExp(Calendar.getInstance().getTimeInMillis() / 1000 + DefaultPeriodSeconds);
    }

    public static UserAuthenticationToken decrypt(String token, String key) {
        try {
            return new ObjectMapper().readValue(
                    new Jose().configuration(
                            JoseBuilders.compactDeserializationBuilder()
                                    .serializedSource(token)
                                    .key(key)
                    ).deserialization(),
                    UserAuthenticationToken.class
            );
        } catch (IOException e) {
            e.printStackTrace();
            log.info("UserAuthenticationToken deserialize failed...");
        }

        return null;
    }

}
