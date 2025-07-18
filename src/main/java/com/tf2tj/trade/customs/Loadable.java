package com.tf2tj.trade.customs;

import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public abstract class Loadable<R> {
     @Setter
     private Date lastUpdatedDate;
     @Setter
     private Duration staleDuration;
     private R resource;

     public boolean isStale() {
          Instant lastUpdatedInstant = lastUpdatedDate.toInstant();
          return lastUpdatedInstant.isAfter(lastUpdatedInstant.plus(staleDuration));
     }

     public abstract void updateResource();

     public R getResource() {
          if (isStale()) {
               updateResource();
          }
          return resource;
     }
}
