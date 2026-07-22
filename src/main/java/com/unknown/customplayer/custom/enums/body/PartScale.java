package com.unknown.customplayer.custom.enums.body;

//  Which ladder of severity a part is measured on. The two are not comparable and never mix.
//  SENSE runs three degrees, of which only the upper two are stored -- degree one is a short timed
//  debuff and lives as a MobEffect, where vanilla already counts it down for us.
//  BODY runs four grades, named for how hard each is to undo.
public enum PartScale {
    SENSE,
    BODY,
}
