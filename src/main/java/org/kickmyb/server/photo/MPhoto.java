package org.kickmyb.server.photo;

import org.joda.time.DateTime;
import org.kickmyb.server.task.MTask;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Lob    public byte[] blob;
    @Basic  public String contentType;
    public Date deleteDate;
    @OneToOne
    public MTask task;
}
