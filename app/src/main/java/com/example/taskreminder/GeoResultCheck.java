package com.example.taskreminder;

import android.location.Address;

public class GeoResultCheck {
    private Address address;

    public GeoResultCheck(Address address)
    {
        this.address = address;
    }

    public Address getAddress(){

        String display_address = "";

        display_address += address.getAddressLine(0) + "\n";

        for(int i = 1; i < address.getMaxAddressLineIndex(); i++)
        {
            display_address += address.getAddressLine(i) + ", ";
        }

        display_address = display_address.substring(0, display_address.length() - 2);

        return address;
    }

    public String toString(){
        String display_address = "";

        if(address.getFeatureName() != null)
        {
           // display_address += address + ", ";
        }

        for(int i = 0; i < address.getMaxAddressLineIndex(); i++)
        {
            System.out.println("Test :: Max address "+i+"  "+address.getAddressLine(i));
            display_address += address.getAddressLine(i)+"\n";
        }

        return display_address;
    }
}
