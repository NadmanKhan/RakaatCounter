package com.example.rakaatcounter

enum class MakeProfileStatus {
    STARTING,
    ALL_VALUES_OKAY,
    NORMAL_VALUES_OKAY,
    ERROR_NOT_COMPLETED_MEASURING_NORMAL_VALUES,
    ERROR_NOT_COMPLETED_MEASURING_SUJOOD_VALUES,
    ERROR_TOO_VARIANT_NORMAL_VALUES,
    ERROR_TOO_VARIANT_SUJOOD_VALUES,
    ERROR_TOO_CLOSE_VALUES,
    ERROR_UNCHECKED_CASE
}