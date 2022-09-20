package com.met.atims_reporter.model

data class UserDetails(
    var user_id: Int,
    var company_id: Int,
    var role_id: String,
    var first_name: String,
    var last_name: String,
    var email: String,
    var phone: String,
    var address: String,
    var city: String,
    var zip: String,
    var role_name: String,
    var profile_image: String,
    var contact_email: String,
    var contact_mobile: String,
    var contact_landline: String
)