package net.teamtrycatch.shared;

import java.io.Serializable;
import java.util.Date;

public interface Transaction extends Serializable {
    public String getDescription();

    public Date getDate();

    public int getAmount();
}
