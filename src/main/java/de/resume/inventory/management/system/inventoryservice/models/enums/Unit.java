package de.resume.inventory.management.system.inventoryservice.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Unit {

    PIECE("piece"),
    KILOGRAM("kilogram"),
    LITER("liter"),
    GRAM("gram"),
    METER("meter"),
    BOX("box"),
    PACKAGE("package"),
    PAIR("pair"),
    DOZEN("dozen");

    private final String name;
}
