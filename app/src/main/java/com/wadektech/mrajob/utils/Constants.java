package com.wadektech.mrajob.utils;

import com.wadektech.mrajob.models.JobSeeker;

public class Constants {
  public static final String JOB_SEEKER_INFO_REFERENCE = "JobSeekerInfo";
  public static final String JOB_SEEKER_LOCATION_REFERENCE = "JobSeekerLocation" ;
  public static JobSeeker currentUser;

  public static String userWelcomeBanner() {
    if (Constants.currentUser != null){
      return new StringBuilder("Welcome ")
          .append(Constants.currentUser.getFirstName())
          .append(" ")
          .append(Constants.currentUser.getLastName()).toString();
    } else {
      return "";
    }
  }
}
