package com.moutamid.jiji.model;

import com.google.android.gms.maps.model.LatLng;

public class UserModel {

    public String name, number, email, id_card_link, tax_certificate_link, registration_certificate_link;
    public LatLng current_location;
    public String user_type;

    public UserModel() {
    }
}
