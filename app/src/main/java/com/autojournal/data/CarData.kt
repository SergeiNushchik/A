package com.autojournal.data

object CarData {

    val carBrands = mapOf(
        // ========== ЕВРОПЕЙСКИЕ МАРКИ ==========
        "Audi" to listOf(
            "A1", "A3", "A4", "A5", "A6", "A7", "A8",
            "Q2", "Q3", "Q5", "Q7", "Q8",
            "e-tron", "e-tron GT", "Q4 e-tron", "Q6 e-tron",
            "TT", "R8", "RS3", "RS4", "RS5", "RS6", "RS7",
            "S3", "S4", "S5", "S6", "S7", "S8", "SQ5", "SQ7", "SQ8"
        ),
        "BMW" to listOf(
            "1 серия", "2 серия", "3 серия", "4 серия", "5 серия", "6 серия", "7 серия", "8 серия",
            "X1", "X2", "X3", "X4", "X5", "X6", "X7", "XM",
            "i3", "i4", "i5", "i7", "iX", "iX1", "iX2", "iX3",
            "Z4", "M2", "M3", "M4", "M5", "M8", "X3 M", "X4 M", "X5 M", "X6 M"
        ),
        "Citroen" to listOf(
            "C1", "C2", "C3", "C4", "C5", "C6", "C8",
            "C3 Aircross", "C4 Cactus", "C4 SpaceTourer", "C5 Aircross",
            "Berlingo", "Jumpy", "Jumper", "SpaceTourer",
            "DS3", "DS4", "DS5", "DS7", "DS9", "E-Mehari", "Ami"
        ),
        "Fiat" to listOf(
            "Panda", "500", "500L", "500X", "500e",
            "Punto", "Bravo", "Stilo", "Tipo", "Doblo", "Ducato", "Scudo", "Ulysse",
            "124 Spider", "Fullback", "Toro", "Argo", "Cronos", "Mobi", "Palio", "Siena"
        ),
        "Ford" to listOf(
            "Fiesta", "Focus", "Mondeo", "Fusion", "Mustang", "Mustang Mach-E",
            "Kuga", "Escape", "Explorer", "Expedition", "Bronco", "Bronco Sport",
            "Ranger", "F-150", "F-250", "F-350", "Maverick", "Transit", "Transit Custom",
            "Galaxy", "S-Max", "C-Max", "B-Max", "Edge", "Puma", "Capri", "Scorpio", "Orion"
        ),
        "Mercedes-Benz" to listOf(
            "A-Class", "B-Class", "C-Class", "E-Class", "S-Class",
            "CLA", "CLS", "EQS", "EQE", "EQB", "EQA", "EQC", "EQV", "EQT",
            "GLA", "GLB", "GLC", "GLE", "GLS", "G-Class", "G-Wagen",
            "AMG GT", "AMG GT 4-Door", "SL", "SLC", "SLK", "CLK", "CL", "CLS",
            "V-Class", "Vito", "Sprinter", "Citan", "Marco Polo", "X-Class"
        ),
        "Opel" to listOf(
            "Corsa", "Astra", "Insignia", "Vectra", "Zafira", "Meriva", "Agila",
            "Mokka", "Mokka-e", "Crossland", "Grandland", "Grandland GSe",
            "Combo", "Combo-e", "Vivaro", "Vivaro-e", "Movano", "Rock-e",
            "Karl", "Adam", "Ampera", "Ampera-e", "GT", "Speedster", "Monza", "Senator", "Omega"
        ),
        "Peugeot" to listOf(
            "208", "308", "408", "508", "2008", "3008", "5008",
            "e-208", "e-308", "e-408", "e-3008", "e-5008",
            "Partner", "Expert", "Boxer", "Rifter", "Traveller", "Landtrek",
            "106", "206", "207", "307", "407", "607", "807", "1007", "4007", "4008"
        ),
        "Renault" to listOf(
            "Clio", "Symbol", "Logan", "Sandero", "Megane", "Megane E-Tech",
            "Fluence", "Latitude", "Talisman", "Kaptur", "Koleos", "Duster", "Arkana",
            "Austral", "Espace", "Scenic", "Grand Scenic", "Twingo", "Zoe",
            "Trafic", "Trafic E-Tech", "Master", "Master E-Tech", "Kangoo", "Kangoo E-Tech",
            "Alaskan", "Oroch", "Sandero Stepway", "Jogger", "Express"
        ),
        "Skoda" to listOf(
            "Fabia", "Rapid", "Octavia", "Superb", "Kamiq", "Karoq", "Kodiaq",
            "Enyaq", "Enyaq Coupe", "Scala", "Slavia", "Kushaq",
            "Yeti", "Roomster", "Citigo", "Citigo-e", "Enyaq iV"
        ),
        "Volkswagen" to listOf(
            "Polo", "Golf", "Jetta", "Passat", "Arteon", "Arteon Shooting Brake",
            "T-Cross", "T-Roc", "Tiguan", "Touareg", "Atlas", "Teramont", "Taos", "Taigo",
            "ID.3", "ID.4", "ID.5", "ID.6", "ID.7", "ID.Buzz",
            "Multivan", "Caravelle", "Caddy", "Caddy Cargo", "Transporter", "Crafter",
            "Up!", "e-up!", "Golf Sportsvan", "Beetle", "Scirocco", "Corrado", "Karmann Ghia"
        ),
        "Volvo" to listOf(
            "S40", "S60", "S80", "S90", "V40", "V60", "V90", "XC40", "XC60", "XC90",
            "C40", "C70", "EX30", "EX90", "EC40", "ES90",
            "P1800", "240", "740", "940", "850", "960", "Amazon", "PV444", "PV544"
        ),
        "Porsche" to listOf(
            "911", "911 Turbo", "911 GT3", "911 GT3 RS", "911 Dakar",
            "718 Boxster", "718 Cayman", "718 Spyder",
            "Panamera", "Taycan", "Taycan Cross Turismo", "Taycan Sport Turismo",
            "Macan", "Cayenne", "Cayenne Coupe",
            "918 Spyder", "Carrera GT"
        ),
        "Jaguar" to listOf(
            "XE", "XF", "XJ", "XK", "F-Type", "E-Pace", "F-Pace", "I-Pace",
            "X-Type", "S-Type", "Daimler", "Mark II", "E-Type", "XJS", "XJ220"
        ),
        "Land Rover" to listOf(
            "Defender", "Defender 90", "Defender 110", "Defender 130",
            "Discovery", "Discovery Sport", "Discovery 5",
            "Range Rover", "Range Rover Sport", "Range Rover Evoque", "Range Rover Velar",
            "Freelander", "LR2", "LR3", "LR4", "Series I", "Series II", "Series III"
        ),
        "Mini" to listOf(
            "Cooper", "Cooper S", "Cooper SE", "One", "John Cooper Works",
            "Clubman", "Countryman", "Convertible", "Roadster", "Coupe",
            "Paceman", "Hatch 3-door", "Hatch 5-door", "Electric"
        ),
        "Smart" to listOf(
            "Fortwo", "Fortwo Cabrio", "Forfour", "EQ Fortwo", "EQ Forfour",
            "Roadster", "Crossblade", "K", "City-Coupe", "Brabus"
        ),

        // ========== НЕМЕЦКИЕ (дополнительно) ==========
        "Alpina" to listOf("B3", "B4", "B5", "B6", "B7", "B8", "D3", "D5", "XB7", "Roadster"),
        "Maybach" to listOf("57", "62", "S-Class", "GLS 600", "EQS 680"),

        // ========== АМЕРИКАНСКИЕ МАРКИ ==========
        "Chevrolet" to listOf(
            "Spark", "Aveo", "Cruze", "Malibu", "Impala", "Camaro", "Corvette",
            "Trax", "Equinox", "Blazer", "Traverse", "Tahoe", "Suburban",
            "Silverado", "Colorado", "Bolt", "Bolt EUV", "Volt",
            "Cavalier", "Cobalt", "HHR", "SSR", "Bel Air", "Chevelle", "Nova", "El Camino"
        ),
        "Chrysler" to listOf(
            "300", "300C", "Pacifica", "Voyager", "Town & Country", "Sebring", "PT Cruiser",
            "Crossfire", "Aspen", "Imperial", "LeBaron", "New Yorker", "Fifth Avenue"
        ),
        "Dodge" to listOf(
            "Caliber", "Dart", "Challenger", "Charger", "Magnum", "Avenger", "Journey",
            "Durango", "Ram", "Nitro", "Viper", "Stealth", "Neon", "Intrepid", "Stratus"
        ),
        "Jeep" to listOf(
            "Wrangler", "Wrangler Unlimited", "Grand Cherokee", "Cherokee", "Compass",
            "Renegade", "Gladiator", "Wagoneer", "Grand Wagoneer", "Liberty", "Patriot",
            "Commander", "Comanche", "CJ-7", "CJ-5", "Willys", "Scrambler"
        ),
        "Tesla" to listOf(
            "Model 3", "Model S", "Model X", "Model Y",
            "Cybertruck", "Roadster", "Semi", "Cyberquad"
        ),
        "Lincoln" to listOf(
            "Navigator", "Aviator", "Nautilus", "Corsair", "Zephyr", "Continental",
            "Town Car", "Mark LT", "Mark VIII", "Aviator", "Blackwood", "LS"
        ),
        "Cadillac" to listOf(
            "Escalade", "Escalade ESV", "Escalade IQ",
            "Lyriq", "Celestiq", "Optiq", "Vistiq",
            "CT4", "CT5", "CT6", "XT4", "XT5", "XT6",
            "DeVille", "Seville", "Eldorado", "Fleetwood", "Brougham", "CTS", "ATS", "STS"
        ),
        "GMC" to listOf(
            "Sierra", "Sierra 2500", "Sierra 3500",
            "Yukon", "Yukon XL", "Terrain", "Acadia", "Canyon", "Savana",
            "Hummer EV", "EV SUV", "Syclone", "Typhoon", "Jimmy", "Sonoma"
        ),
        "Buick" to listOf(
            "Encore", "Encore GX", "Envision", "Enclave", "Regal", "LaCrosse", "Verano",
            "Century", "LeSabre", "Park Avenue", "Riviera", "Skylark", "Electra", "Grand National"
        ),
        "Rivian" to listOf("R1T", "R1S", "R2", "R3", "R3X"),
        "Lucid" to listOf("Air", "Gravity"),
        "Fisker" to listOf("Ocean", "Karma", "EMotion", "Pear", "Alaska"),

        // ========== АЗИАТСКИЕ МАРКИ ==========
        "Acura" to listOf(
            "Integra", "RSX", "TSX", "TL", "RL", "ILX", "TLX", "RLX",
            "RDX", "MDX", "ZDX", "NSX", "CDX", "ADX", "ZDX", "SLX"
        ),
        "Daihatsu" to listOf(
            "Charade", "Sirion", "Terios", "Gran Max", "Xenia", "Ayla", "Sigra", "Rocky",
            "Mira", "Cuore", "Applause", "Feroza", "Rugger", "Taft", "Hijet", "Atrai"
        ),
        "Honda" to listOf(
            "Civic", "Accord", "CR-V", "HR-V", "Pilot", "Odyssey", "Fit", "City",
            "Insight", "Passport", "Ridgeline", "Element", "CR-Z", "Clarity",
            "NSX", "S2000", "Prelude", "Integra", "Legend", "Vezel", "Freed", "Stepwgn",
            "Acty", "N-Box", "N-WGN", "Zest", "Vamos", "e", "Prologue"
        ),
        "Hyundai" to listOf(
            "Accent", "Elantra", "Sonata", "Tucson", "Santa Fe", "Palisade",
            "Kona", "Kona Electric", "Creta", "Ioniq", "Ioniq 5", "Ioniq 6", "Ioniq 9",
            "Nexo", "Staria", "Stargazer", "Grandeur", "Azera", "Genesis", "Equus",
            "Veloster", "Tiburon", "Coupe", "Excel", "Pony", "Galloper", "Terracan"
        ),
        "Infiniti" to listOf(
            "G", "G35", "G37", "Q", "Q50", "Q60", "Q70",
            "QX", "QX50", "QX55", "QX60", "QX70", "QX80", "ESQ",
            "I30", "I35", "M", "M35", "M45", "JX", "FX", "EX"
        ),
        "Isuzu" to listOf(
            "D-Max", "Rodeo", "Trooper", "VehiCross", "Ascender", "Axiom", "Hombre",
            "Elf", "N-Series", "F-Series", "Giga", "Forward", "Piazza", "Amigo", "Mu"
        ),
        "Kia" to listOf(
            "Picanto", "Rio", "Ceed", "Cerato", "K3", "K4", "K5", "K6", "K7", "K8", "K9",
            "Optima", "Stinger", "Sportage", "Sorento", "Telluride", "Mohave",
            "Niro", "Niro EV", "Soul", "Soul EV", "EV3", "EV5", "EV6", "EV9",
            "Stonic", "Xceed", "ProCeed", "Forte", "Spectra", "Magentis", "Carens", "Carnival"
        ),
        "Lexus" to listOf(
            "CT", "IS", "ES", "GS", "LS", "LC", "RC", "SC",
            "UX", "NX", "RX", "GX", "LX", "RZ", "TX", "LBX",
            "HS", "LFA", "IS F", "GS F", "RC F", "RCF", "UX 300e", "NX 450h+", "RX 500h"
        ),
        "Mazda" to listOf(
            "2", "3", "6", "CX-3", "CX-30", "CX-5", "CX-60", "CX-70", "CX-80", "CX-90",
            "MX-5", "MX-30", "RX-7", "RX-8", "MX-3", "MX-6", "MPV", "Protege", "Familia",
            "B-Series", "BT-50", "Tribute", "Millenia", "Cosmo", "Luce", "Roadster", "Carol"
        ),
        "Mitsubishi" to listOf(
            "Colt", "Lancer", "Lancer Evolution", "Pajero", "Pajero Sport",
            "Outlander", "Outlander PHEV", "ASX", "Eclipse", "Eclipse Cross",
            "L200", "Triton", "Delica", "Galant", "Space Runner", "Space Wagon",
            "Pajero Mini", "Pajero iO", "Montero", "Shogun", "Starion", "3000GT", "FTO"
        ),
        "Nissan" to listOf(
            "Almera", "Sentra", "Altima", "Maxima", "Skyline", "GT-R",
            "Juke", "Qashqai", "X-Trail", "Murano", "Pathfinder", "Patrol", "Armada",
            "Navara", "Frontier", "Titan", "Leaf", "Ariya", "Z", "370Z", "350Z",
            "Micra", "March", "Note", "Pulsar", "Primera", "Bluebird", "Laurel", "Cefiro", "Gloria"
        ),
        "Subaru" to listOf(
            "Impreza", "Legacy", "Outback", "Forester", "XV", "Crosstrek", "Ascent",
            "BRZ", "WRX", "WRX STI", "Levorg", "Baja", "Tribeca", "SVX", "XT",
            "Loyale", "Justy", "Vivio", "Pleo", "Dex", "Stella", "R1", "R2", "Sambar"
        ),
        "Suzuki" to listOf(
            "Swift", "Baleno", "SX4", "Vitara", "Grand Vitara", "Jimny", "Ignis",
            "Ciaz", "Across", "Ertiga", "XL6", "Spacia", "Hustler", "Xbee",
            "Alto", "Celerio", "Wagon R", "Every", "Carry", "Samurai", "Sidekick", "X-90"
        ),
        "Toyota" to listOf(
            "Corolla", "Camry", "Prius", "Prius Prime", "Prius C", "Prius V",
            "RAV4", "RAV4 Prime", "Highlander", "Land Cruiser", "Prado", "Sequoia", "4Runner",
            "Hilux", "Tacoma", "Tundra", "Supra", "GR86", "GR Corolla", "GR Yaris",
            "Yaris", "Auris", "Avensis", "C-HR", "bZ4X", "Mirai", "Crown", "Corolla Cross",
            "Sienna", "Previa", "Estima", "Alphard", "Vellfire", "Hiace", "Dyna", "Toyoace",
            "Celica", "MR2", "Starlet", "Tercel", "Paseo", "Cressida", "Chaser", "Mark II"
        ),
        "Datsun" to listOf(
            "Go", "Go+", "mi-DO", "on-DO", "240Z", "260Z", "280Z", "510", "Bluebird", "Sunny", "Cherry"
        ),

        // ========== КОРЕЙСКИЕ МАРКИ ==========
        "Genesis" to listOf(
            "G70", "G80", "G90", "GV60", "GV70", "GV80", "GV90",
            "Electrified GV70", "Electrified G80", "X", "Speedium", "Neolun"
        ),
        "SsangYong" to listOf(
            "Actyon", "Actyon Sports", "Korando", "Rexton", "Tivoli", "XLV", "Korando EV",
            "Musso", "Rodius", "Stavic", "Kyron", "Chairman", "Kallista", "Nomad"
        ),

        // ========== КИТАЙСКИЕ МАРКИ ==========
        "BYD" to listOf(
            "Atto 3", "Dolphin", "Seal", "Han", "Tang", "Yuan", "Song", "Qin", "e6",
            "Seagull", "N7", "N8", "U8", "U9", "Yangwang", "FangChengBao", "Denza D9"
        ),
        "Chery" to listOf(
            "Tiggo 2", "Tiggo 4", "Tiggo 7", "Tiggo 8", "Tiggo 9",
            "Arrizo 6", "Arrizo 8", "Omoda 5", "Omoda C5", "Omoda E5",
            "Exeed LX", "Exeed TX", "Exeed VX", "Exeed RX", "Exeed ET",
            "iCar 03", "Jaguar", "Karry", "Cowin", "Riich"
        ),
        "Geely" to listOf(
            "Emgrand 7", "Emgrand X7", "Emgrand", "GS", "GL", "GC", "GE", "GX", "EX",
            "Atlas", "Atlas Pro", "Coolray", "Tugella", "Monjaro", "Azkarra", "Preface",
            "Geometry", "Geometry C", "Geometry A", "Geometry E", "Geometry EX3",
            "Zeekr 001", "Zeekr 009", "Lynk & Co 01", "Lynk & Co 02", "Lynk & Co 03", "Lynk & Co 05"
        ),
        "Great Wall" to listOf(
            "Haval H6", "Haval F7", "Haval F7x", "Haval M6", "Haval Jolion", "Haval Dargo",
            "Haval H9", "Haval H5", "Haval H2", "Haval H1", "Haval P04",
            "Tank 300", "Tank 500", "Tank 700", "Poer", "Wey", "Ora", "Ora Good Cat", "Ora Funky Cat"
        ),
        "Xiaomi" to listOf("SU7", "SU7 Pro", "SU7 Max", "SU7 Ultra"),
        "NIO" to listOf("ET5", "ET7", "EC6", "EC7", "ES6", "ES7", "ES8", "EL6", "EL7", "EL8", "ET9"),
        "XPeng" to listOf("G3", "G6", "G9", "P5", "P7", "X9", "X2", "L7", "L8", "L9"),
        "Li Auto" to listOf("L6", "L7", "L8", "L9", "MEGA", "ONE"),
        "Aion" to listOf("LX", "LX Plus", "V", "V Plus", "S", "S Plus", "Y", "Y Plus", "Hyper GT", "Hyper SSR"),
        "Hongqi" to listOf("H5", "H9", "E-HS3", "E-HS9", "LS7", "HS5", "HS7", "HQ9", "E-QM5", "E-QM5 Plus"),
        "MG" to listOf(
            "3", "5", "6", "7", "ZS", "ZS EV", "HS", "HS PHEV", "Hector", "Astor", "Comet",
            "Cyberster", "EHS", "EZS", "Marvel R", "Mulan", "4 EV", "5 EV", "ZS EV"
        ),
        "Omoda" to listOf("C5", "E5", "C7", "O5", "O7", "S5", "C9"),
        "Jaecoo" to listOf("J7", "J8", "J5", "J3", "O1", "O3"),
        "Zeekr" to listOf("001", "007", "009", "X", "Mix", "Z", "L", "S", "V"),
        "Leapmotor" to listOf("C01", "C11", "C10", "T03", "S01", "C16", "C13", "C9"),
        "AITO" to listOf("M5", "M7", "M9", "M3", "M8", "S1", "S3", "S5"),
        "Changan" to listOf(
            "CS35 Plus", "CS55 Plus", "CS75 Plus", "CS85", "CS95",
            "UNI-K", "UNI-K iDD", "UNI-T", "UNI-V", "UNI-Z",
            "BenBen", "Eado", "Raeton", "Alsvin", "Oshan X7", "Oshan Z6", "Lamore"
        ),
        "BAIC" to listOf(
            "Beijing X3", "Beijing X5", "Beijing X7", "Beijing BJ40", "Beijing BJ60", "Beijing BJ80",
            "EU5", "EU7", "EC3", "EC5", "EX3", "EX5", "Arcfox αT", "Arcfox αS"
        ),
        "Dongfeng" to listOf(
            "Aeolus", "Aeolus AX7", "Aeolus Yixuan", "Aeolus E70",
            "Forthing", "Fengguang", "Rich", "Box", "Glory 580", "M5", "M7", "EX1"
        ),
        "GAC" to listOf(
            "Trumpchi GS3", "Trumpchi GS4", "Trumpchi GS8", "Trumpchi M8", "Trumpchi M6",
            "Emkoo", "Aion S", "Aion Y", "Aion V", "Aion LX", "Aion Hyper", "E9"
        ),
        "Roewe" to listOf(
            "RX3", "RX5", "RX5 Max", "RX8", "i5", "i6", "iMAX8", "Marvel X", "Evo", "Ei5", "ER6"
        ),
        "Maxus" to listOf(
            "D60", "D90", "G10", "G20", "G50", "G70", "Mifa 9", "T60", "T90", "T70", "Deliver", "Euniq"
        ),

        // ========== БРИТАНСКИЕ МАРКИ ==========
        "Aston Martin" to listOf(
            "DB5", "DB6", "DB7", "DB9", "DB10", "DB11", "DB12",
            "Vantage", "Vanquish", "DBS", "Rapide", "Virage", "Lagonda",
            "Valhalla", "Valkyrie", "Valiant", "Victor", "DBS Superleggera"
        ),
        "Bentley" to listOf(
            "Continental GT", "Continental GTC", "Continental Flying Spur",
            "Bentayga", "Mulsanne", "Flying Spur", "Azure", "Arnage", "Turbo R",
            "Bacalar", "Batur", "Mulliner", "Blower", "Birkin", "R-Type", "S-Type"
        ),
        "Lotus" to listOf(
            "Elise", "Exige", "Evora", "Emira", "Eletre", "Evija",
            "Esprit", "Europa", "Elan", "Carlton", "Seven", "Elite", "Eclat", "Excel"
        ),
        "McLaren" to listOf(
            "540C", "570GT", "570S", "600LT", "620R", "650S", "675LT",
            "720S", "750S", "765LT", "Senna", "Speedtail", "Elva", "Artura", "GT",
            "P1", "F1", "M6GT", "MP4-12C", "12C", "25", "30", "50"
        ),
        "Rolls-Royce" to listOf(
            "Phantom", "Ghost", "Cullinan", "Wraith", "Dawn", "Spectre",
            "Silver Cloud", "Silver Shadow", "Silver Spirit", "Silver Seraph", "Corniche", "Camargue",
            "Park Ward", "Mulliner", "Bespoke", "Boat Tail", "Sweptail", "Droptail"
        ),

        // ========== ИТАЛЬЯНСКИЕ МАРКИ ==========
        "Alfa Romeo" to listOf(
            "Giulia", "Stelvio", "Tonale", "Giulietta", "Mito", "4C", "8C",
            "Spider", "GTV", "Brera", "159", "147", "156", "166", "75", "90", "33", "Alfasud", "Alfetta"
        ),
        "Ferrari" to listOf(
            "296 GTB", "296 GTS", "SF90 Stradale", "SF90 Spider", "SF90 XX",
            "Roma", "Roma Spider", "Portofino", "Portofino M", "Purosangue",
            "812 Superfast", "812 GTS", "12Cilindri", "F8 Tributo", "F8 Spider",
            "488", "488 Pista", "488 Spider", "458", "458 Speciale", "458 Spider",
            "LaFerrari", "Enzo", "F40", "F50", "Testarossa", "Daytona", "250 GTO"
        ),
        "Lamborghini" to listOf(
            "Aventador", "Aventador S", "Aventador SV", "Aventador SVJ", "Aventador LP780",
            "Huracan", "Huracan EVO", "Huracan STO", "Huracan Sterrato", "Huracan Tecnica",
            "Revuelto", "Temerario", "Urus", "Urus S", "Urus Performante", "Urus SE",
            "Countach", "Diablo", "Murcielago", "Gallardo", "Miura", "Espada", "Jarama", "LM002"
        ),
        "Maserati" to listOf(
            "Ghibli", "Quattroporte", "Levante", "MC20", "Grecale", "GranTurismo", "GranCabrio",
            "Biturbo", "3200GT", "4200GT", "Spyder", "Coupe", "Merak", "Bora", "Mistral", "Khamsin"
        ),
        "Pagani" to listOf(
            "Zonda", "Huayra", "Utopia", "Zonda R", "Zonda Cinque", "Zonda Tricolore", "Imola"
        ),

        // ========== ФРАНЦУЗСКИЕ МАРКИ ==========
        "Bugatti" to listOf(
            "Veyron", "Chiron", "Divo", "Centodieci", "Bolide", "La Voiture Noire",
            "EB110", "Type 35", "Type 41 Royale", "Pur Sang", "Tourbillon"
        ),

        // ========== РОССИЙСКИЕ МАРКИ ==========
        "Lada" to listOf(
            "Granta", "Granta Sport", "Granta Cross", "Vesta", "Vesta Sport", "Vesta SW", "Vesta Cross",
            "Kalina", "Kalina Sport", "Kalina Cross", "Priora", "Priora Sport",
            "Largus", "Largus Cross", "X-Ray", "X-Ray Cross", "X-Cross",
            "Niva", "Niva Travel", "Niva Legend", "Niva Urban", "Niva Bronto",
            "2101", "2102", "2103", "2104", "2105", "2106", "2107",
            "2108", "2109", "2110", "2111", "2112", "2113", "2114", "2115",
            "Oka", "Samara", "Revolution", "Island", "e-Largus", "e-Niva"
        ),
        "ГАЗ" to listOf(
            "ГАЗель", "ГАЗель NEXT", "ГАЗель Бизнес", "ГАЗель Фермер", "ГАЗель Некст",
            "Соболь", "Соболь 4x4", "Соболь NN",
            "Валдай", "ГАЗ-53", "ГАЗ-3307", "ГАЗ-3308", "ГАЗ-66", "ГАЗ-3309",
            "ГАЗ-3102", "ГАЗ-31029", "ГАЗ-3110", "ГАЗ-31105", "Волга", "Сайбер"
        ),
        "УАЗ" to listOf(
            "Патриот", "Патриот Спорт", "Патриот Pro",
            "Пикап", "Профи", "Карго",
            "Хантер", "Буханка", "469", "3151", "3160", "3163",
            "2350", "2360", "2970", "3741", "3962", "3909"
        ),
        "Москвич" to listOf("Москвич 3", "Москвич 3e", "Москвич 5", "Москвич 6", "Москвич 8", "Святогор", "Иван Калита"),
        "Aurus" to listOf("Senat", "Senat Limousine", "Senat TS", "Komendant", "L-700", "V-700", "Kortezh"),

        // ========== ДРУГИЕ МАРКИ ==========
        "Abarth" to listOf("500", "500C", "595", "695", "124 Spider", "1500", "2000", "Scorpione", "Punto", "Grande Punto"),
        "Aixam" to listOf("City", "Coupe", "Crossline", "e-Coupe", "Sensation", "Scenic", "Scouty"),
        "GWM" to listOf("Haval H6", "Haval H9", "Haval F7", "Haval F7x", "Ora", "Wey", "Tank 300", "Tank 500", "Poer"),
        "Lancia" to listOf("Delta", "Ypsilon", "Thesis", "Lybra", "Musa", "Phedra", "Dedra", "Kappa", "Thema", "Beta", "Stratos", "Fulvia", "Aurelia"),
        "Ligier" to listOf("JS50", "JS60", "JS80", "X-Too", "Myli", "Be Up", "Ambra", "Duchi", "Pulse", "Scooty", "Nova"),
        "Sollers" to listOf("Argo", "Atlant", "Proxima", "Aurus", "Sollers", "ST6", "Sollers V", "Sollers T", "Sollers F"),
        "Zotye" to listOf("T600", "T700", "T800", "Z500", "Z700", "E200", "E300", "Nesla", "Coupa", "Tango"),
        "Foton" to listOf("Tunland", "Sauvana", "Toano", "Tianhang", "Aumark", "BXT", "ETX", "Foton", "Midi", "View"),
        "Proton" to listOf("Persona", "Saga", "X50", "X70", "X90", "Iriz", "Exora", "Preve", "Suprima", "Gen-2", "Savvy"),
        "Perodua" to listOf("Myvi", "Axia", "Bezza", "Alza", "Ativa", "Aruz", "Kenari", "Kancil", "Nippa", "Viva"),
        "Koenigsegg" to listOf("CCX", "CCR", "Agera", "Agera RS", "Regera", "Gemera", "Jesko", "CCGT", "Tre Kronor"),
        "Hennessey" to listOf("Venom F5", "Venom GT", "Exorcist", "Mammoth", "VelociRaptor", "Maximus", "Pony", "Raptor", "Fenyr"),
        "Brabus" to listOf("Rocket", "E V12", "G V12", "B63", "B40", "B50", "M V12", "S V12", "GL V12", "CL V12", "SL V12")
    )

    // Список марок (отсортированный)
    val brands = carBrands.keys.sorted()
}