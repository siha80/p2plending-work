package com.skp.payment.p2plending.user.ui.event.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by skplanet on 2014-12-15.
 */
@Data
@XmlRootElement
public class P2pLendingError implements Serializable {
    private static final long serialVersionUID = -6491783623773679322L;

    private final List<P2pLendingErrorDetail> errors = new ArrayList<>();
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private String code;
    private String message;

    public void addErrors(P2pLendingErrorDetail e){
        this.errors.add(e);
    }
}
