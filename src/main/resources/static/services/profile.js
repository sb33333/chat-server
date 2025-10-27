"use strict";

let profile = {};

export function setProfile (username, imgId) {
  profile = Object.assign({}, profile, {username, imgId});
}

export function getProfile () {
    return Object.assign({}, profile);
}