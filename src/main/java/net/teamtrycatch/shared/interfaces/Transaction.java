package net.teamtrycatch.shared.interfaces;

import java.io.Serializable;
import java.util.Date;

public interface Transaction extends Serializable {
    public String getDescription();

    public Date getDate();

    public int getAmount();
}
