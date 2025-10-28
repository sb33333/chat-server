"use strict";

const KEY_USERNAME = "username";
const KEY_IMG_ID = "imgId";

export function setUsername (username) {
  sessionStorage.setItem(KEY_USERNAME, username);
}

export function getUsername () {
  sessionStorage.getItem(KEY_USERNAME);
}

export function setProfileImageId(imgId) {
  sessionStorage.setItem(KEY_IMG_ID, imgId);
}


export function getProfileImageId() {
  sessionStorage.getItem(KEY_IMG_ID);
}