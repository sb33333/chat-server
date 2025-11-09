"use strict";

const KEY_USERNAME = "username";
const KEY_IMG_ID = "imgId";
const KEY_GRADIENT = "gradient";

export function setUsername (username) {
  sessionStorage.setItem(KEY_USERNAME, username);
}

export function getUsername () {
  return sessionStorage.getItem(KEY_USERNAME);
}

export function setProfileImageId(imgId) {
  sessionStorage.setItem(KEY_IMG_ID, imgId);
}
export function getProfileImageId() {
  return sessionStorage.getItem(KEY_IMG_ID);
}

export function setProfileGradient(gradient) {
  sessionStorage.setItem(KEY_GRADIENT, gradient);
}

export function getProfileGradient() {
  return sessionStorage.getItem(KEY_GRADIENT);
}