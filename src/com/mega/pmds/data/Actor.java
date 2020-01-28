package com.mega.pmds.data;

public enum Actor {
	PLAYER(0x00),			TEAMMATE_2(0x0A),		TEAMMATE_3(0x0B),		INHABITANT_0(0X0E),
	INHABITANT_1(0X0F),		INHABITANT_2(0X10),		INHABITANT_3(0X11),		INHABITANT_4(0X12),
	INHABITANT_5(0X13),		INHABITANT_6(0X14),		INHABITANT_7(0X15),		INHABITANT_8(0X16),
	INHABITANT_9(0X17),		INHABITANT_A(0X18),		INHABITANT_B(0X19),		INHABITANT_C(0X1A),
	INHABITANT_D(0X1B),		INHABITANT_E(0X1C),		INHABITANT_F(0X1D),		CLIENT_0(0X1E),
	CLIENT_1(0X1F),			PARTNER(0x22),			RANDOM_STARTER(0x24),	WARTORTLE(0x25),
	BLASTOISE_0(0x26),		MEOWTH(0x27),			LICKITUNG(0x28),		JIRACHI_0(0x29),
	HYPNO(0x2A),			NOCTOWL(0x2B),			METAPOD_0(0x2C),		JIRACHI_1(0x2D),
	WIGGLYTUFF_0(0x2E),		PELIPPER_0(0x2F),		PELIPPER_1(0x30),		PELIPPER_2(0x31),
	PELIPPER_3(0x32),		AZUMARILL(0x33),		BUTTERFREE(0x35),		CATERPIE(0x36),
	PELIPPER_4(0x37),		PELIPPER_5(0x38),		PELIPPER_6(0x39),		PELIPPER_7(0x3A),
	PELIPPER_8(0x3B),		MAGNEMITE_0(0x3C),		MAGNEMITE_1(0x3D),		MAGNEMITE_2(0x3E),
	MEGNETON(0x3F),			MAGNEMITE_3(0x40),		MAGNEMITE_4(0x41),		DUGTRIO_0(0x42),
	DUGTRIO_1(0x43),		DIGLETT(0x44),			SKARMORY(0x45),			GREEN_KECLEON(0x46),
	PURPLE_KECLEON(0x47),	PERSIAN(0x48),			WIGGLYTUFF_1(0x49),		GULPIN_0(0x4A),
	KANGASKHAN(0x4B),		GULPIN_1(0x4C),			LOMBRE(0x4D),			JUMPLUFF_0(0x4E),
	BELLSPROUT(0x4F),		SNUBBULL(0x50),			GRANBULL(0x51),			GARDEVOIR(0x52),
	ABSOL(0x53),			MAKUHITA(0x54),			SHIFTRY(0x55),			NUZLEAF_0(0x56),
	NUZLEAF_1(0x57),		ALAKAZAM(0x58),			CHARIZARD(0x59),		TYRANITAR(0x5A),
	GENGAR(0x5B),			EKANS(0x5C),			MEDICHAM(0x5D),			METAPOD_1(0x5E),
	JUMPLUFF_1(0x5F),		ZAPDOS(0x60),			XATU(0x61),				WISHCASH(0x62),
	NINETAILS(0x63),		DECOY(0x64),			MOLTRES(0x65),			ARTICUNO(0x66),
	GROUDON(0x67),			BLASTOISE_1(0x68),		OCTILLERY(0x69),		GOLEM(0x6A),
	BLASTOISE_2(0x6B),		RAYQUAZA(0x6C),			WYNAUT(0x6D),			WOBBUFFET(0x6E),
	MANKEY_0(0x6F),			MANKEY_1(0x70),			MANKEY_2(0x71),			MANKEY_3(0x72),
	SPINDA(0x73),			ENTEI(0x74),			RAIKOU(0x75),			SUICUNE(0x76),
	HO_OH(0x77),			MEWTWO(0x78),			LATIOS(0x79),			LATIAS(0x7A),
	JIRACHI_2(0x7B),		SMEARGLE_0(0x7C),		SMEARGLE_1(0x7D),		SMEARGLE_2(0x7E),
	MUNCHLAX(0x7F),			MEW(0x80),				REGIROCK(0x81),			REGICE(0x82),
	REGISTEEL(0x83),		KYOGRE(0x84),			LUGIA(0x85),			DEOXYS(0x86),
	RAICHU(0x87),			GOLBAT(0x88),			RHYDON(0x89),			MR_MIME(0x8A),
	SCYTHER(0x8B),			PINSIR(0x8C),			MEGANIUM(0x8D),			AIPOM(0x8E),
	PHANPY(0x8F),			MERCHANT_0(0x90),		MERCHANT_1(0x91),		DONPHAN(0x93);
	
	private int id;
	
	private Actor(int idIn) {
		this.id = idIn;
	}
	
	@Override
	public String toString() {
		String name = super.toString().toLowerCase();
		char[] temp = name.toCharArray();
		temp[0] = Character.toUpperCase(temp[0]);
		name = String.copyValueOf(temp);
		if(name.equals("Ho_oh"))
			name="Ho-oh";
		if(name.equals("Mr_mime"))
			name="Mr. Mime";
		if(name.equals("Green_kecleon"))
			name="Green Kecleon";
		if(name.equals("Purple_kecleon"))
			name="Purple Kecleon";
		name.replaceAll("_", " ");
		return name;
	}
	
	public static Actor fromID(int id) {
		for(Actor act : Actor.values()) {
			if ((act.id) == (id)) {
				return act;
			}
		}
		return null;
	}
}
