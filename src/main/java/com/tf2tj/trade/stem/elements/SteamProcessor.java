package com.tf2tj.trade.stem.elements;

import com.tf2tj.trade.models.people.TF2TUser;
import com.tf2tj.trade.stem.requests.HttpRequestBrowser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Steam trading class.
 *
 * @author Ihor Sytnik
 */
@Component
public class SteamProcessor {

    @Autowired
    private HttpRequestBrowser steamBrowser;
    @Autowired
    private TF2TUser tUser;

    public void setUser() {
    }

}
