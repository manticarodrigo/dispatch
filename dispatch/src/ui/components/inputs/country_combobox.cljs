(ns ui.components.inputs.country-combobox
  (:require [ui.components.inputs.combobox :refer (combobox)]))

(def countries
  [{:label "Afghanistan" :value "AF" :code "93"}
   {:label "Albania" :value "AL" :code "355"}
   {:label "Algeria" :value "DZ" :code "213"}
   {:label "American Samoa" :value "AS" :code "1-684"}
   {:label "Andorra" :value "AD" :code "376"}
   {:label "Angola" :value "AO" :code "244"}
   {:label "Anguilla" :value "AI" :code "1-264"}
   {:label "Argentina" :value "AR" :code "54"}
   {:label "Armenia" :value "AM" :code "374"}
   {:label "Aruba" :value "AW" :code "297"}
   {:label "Australia" :value "AU" :code "61"}
   {:label "Azerbaijan" :value "AZ" :code "994"}
   {:label "Bahamas" :value "BS" :code "1-242"}
   {:label "Bahrain" :value "BH" :code "973"}
   {:label "Bangladesh" :value "BD" :code "880"}
   {:label "Barbados" :value "BB" :code "1-246"}
   {:label "Belarus" :value "BY" :code "375"}
   {:label "Belgium" :value "BE" :code "32"}
   {:label "Belize" :value "BZ" :code "501"}
   {:label "Benin" :value "BJ" :code "229"}
   {:label "Bermuda" :value "BM" :code "1-441"}
   {:label "Bhutan" :value "BT" :code "975"}
   {:label "Bolivia" :value "BO" :code "591"}
   {:label "Bosnia and Herzegovina" :value "BA" :code "387"}
   {:label "Botswana" :value "BW" :code "267"}
   {:label "Brazil" :value "BR" :code "55"}
   {:label "British Virgin Islands" :value "VG" :code "1-284"}
   {:label "Brunei" :value "BN" :code "673"}
   {:label "Bulgaria" :value "BG" :code "359"}
   {:label "Burkina Faso" :value "BF" :code "226"}
   {:label "Burundi" :value "BI" :code "257"}
   {:label "Cambodia" :value "KH" :code "855"}
   {:label "Cameroon" :value "CM" :code "237"}
   {:label "Canada" :value "CA" :code "1"}
   {:label "Cape Verde" :value "CV" :code "238"}
   {:label "Cayman Islands" :value "KY" :code "1-345"}
   {:label "Central African Republic" :value "CF" :code "236"}
   {:label "Chad" :value "TD" :code "235"}
   {:label "Chile" :value "CL" :code "56"}
   {:label "China" :value "CN" :code "86"}
   {:label "Columbia" :value "CO" :code "57"}
   {:label "Comoros" :value "KM" :code "269"}
   {:label "Congo" :value "CG" :code "242"}
   {:label "Cook Islands" :value "CK" :code "682"}
   {:label "Costa Rica" :value "CR" :code "506"}
   {:label "Croatia" :value "HR" :code "385"}
   {:label "Cuba" :value "CU" :code "53"}
   {:label "Cyprus" :value "CY" :code "357"}
   {:label "Czech Republic" :value "CZ" :code "420"}
   {:label "Democratic Republic of the Congo" :value "CD" :code "243"}
   {:label "Denmark" :value "DK" :code "45"}
   {:label "Djibouti" :value "DJ" :code "253"}
   {:label "Dominica" :value "DM" :code "1-767"}
   {:label "Dominican Republic" :value "DO" :code "1-809"}
   {:label "East Timor" :value "TL" :code "670"}
   {:label "Ecuador" :value "EC" :code "593"}
   {:label "Egypt" :value "EG" :code "20"}
   {:label "El Salvador" :value "SV" :code "503"}
   {:label "Equatorial Guinea" :value "GQ" :code "240"}
   {:label "Eritrea" :value "ER" :code "291"}
   {:label "Estonia" :value "EE" :code "372"}
   {:label "Ethiopia" :value "ET" :code "251"}
   {:label "Falkland Islands" :value "FK" :code "500"}
   {:label "Faroe Islands" :value "FO" :code "298"}
   {:label "Fiji" :value "FJ" :code "679"}
   {:label "Finland" :value "FI" :code "358"}
   {:label "France" :value "FR" :code "33"}
   {:label "French Polynesia" :value "PF" :code "689"}
   {:label "Gabon" :value "GA" :code "241"}
   {:label "Gambia" :value "GM" :code "220"}
   {:label "Georgia" :value "GE" :code "995"}
   {:label "Germany" :value "DE" :code "49"}
   {:label "Ghana" :value "GH" :code "233"}
   {:label "Gibraltar" :value "GI" :code "350"}
   {:label "Greece" :value "GR" :code "30"}
   {:label "Greenland" :value "GL" :code "299"}
   {:label "Grenada" :value "GD" :code "1-473"}
   {:label "Guam" :value "GU" :code "1-671"}
   {:label "Guatemala" :value "GT" :code "502"}
   {:label "Guinea" :value "GN" :code "224"}
   {:label "Guinea-Bissau" :value "GW" :code "245"}
   {:label "Guyana" :value "GY" :code "592"}
   {:label "Haiti" :value "HT" :code "509"}
   {:label "Honduras" :value "HN" :code "504"}
   {:label "Hong Kong" :value "HK" :code "852"}
   {:label "Hungary" :value "HU" :code "36"}
   {:label "Iceland" :value "IS" :code "354"}
   {:label "India" :value "IN" :code "91"}
   {:label "Indonesia" :value "ID" :code "62"}
   {:label "Iran" :value "IR" :code "98"}
   {:label "Iraq" :value "IQ" :code "964"}
   {:label "Ireland" :value "IE" :code "353"}
   {:label "Israel" :value "IL" :code "972"}
   {:label "Italy" :value "IT" :code "39"}
   {:label "Ivory Coast" :value "CI" :code "225"}
   {:label "Jamaica" :value "JM" :code "1-876"}
   {:label "Japan" :value "JP" :code "81"}
   {:label "Jordan" :value "JO" :code "962"}
   {:label "Kazakhstan" :value "KZ" :code "7"}
   {:label "Kenya" :value "KE" :code "254"}
   {:label "Kiribati" :value "KI" :code "686"}
   {:label "Kosovo" :value "XK" :code "383"}
   {:label "Kuwait" :value "KW" :code "965"}
   {:label "Kyrgyzstan" :value "KG" :code "996"}
   {:label "Laos" :value "LA" :code "856"}
   {:label "Latvia" :value "LV" :code "371"}
   {:label "Lebanon" :value "LB" :code "961"}
   {:label "Lesotho" :value "LS" :code "266"}
   {:label "Liberia" :value "LR" :code "231"}
   {:label "Libya" :value "LY" :code "218"}
   {:label "Liechtenstein" :value "LI" :code "423"}
   {:label "Lithuania" :value "LT" :code "370"}
   {:label "Luxembourg" :value "LU" :code "352"}
   {:label "Macao" :value "MO" :code "853"}
   {:label "Macedonia" :value "MK" :code "389"}
   {:label "Madagascar" :value "MG" :code "261"}
   {:label "Malawi" :value "MW" :code "265"}
   {:label "Malaysia" :value "MY" :code "60"}
   {:label "Maldives" :value "MV" :code "960"}
   {:label "Mali" :value "ML" :code "223"}
   {:label "Malta" :value "MT" :code "356"}
   {:label "Marshall Islands" :value "MH" :code "692"}
   {:label "Mauritania" :value "MR" :code "222"}
   {:label "Mauritius" :value "MU" :code "230"}
   {:label "Mexico" :value "MX" :code "52"}
   {:label "Micronesia" :value "FM" :code "691"}
   {:label "Moldova" :value "MD" :code "373"}
   {:label "Monaco" :value "MC" :code "377"}
   {:label "Mongolia" :value "MN" :code "976"}
   {:label "Montenegro" :value "ME" :code "382"}
   {:label "Montserrat" :value "MS" :code "1-664"}
   {:label "Morocco" :value "MA" :code "212"}
   {:label "Mozambique" :value "MZ" :code "258"}
   {:label "Myanmar" :value "MM" :code "95"}
   {:label "Namibia" :value "NA" :code "264"}
   {:label "Nauru" :value "NR" :code "674"}
   {:label "Nepal" :value "NP" :code "977"}
   {:label "Netherlands" :value "NL" :code "31"}
   {:label "New Caledonia" :value "NC" :code "687"}
   {:label "New Zealand" :value "NZ" :code "64"}
   {:label "Nicaragua" :value "NI" :code "505"}
   {:label "Niger" :value "NE" :code "227"}
   {:label "Nigeria" :value "NG" :code "234"}
   {:label "Niue" :value "NU" :code "683"}
   {:label "Norfolk Island" :value "NF" :code "672"}
   {:label "North Korea" :value "KP" :code "850"}
   {:label "Northern Mariana Islands" :value "MP" :code "1-670"}
   {:label "Norway" :value "NO" :code "47"}
   {:label "Oman" :value "OM" :code "968"}
   {:label "Pakistan" :value "PK" :code "92"}
   {:label "Palau" :value "PW" :code "680"}
   {:label "Palestine" :value "PS" :code "970"}
   {:label "Panama" :value "PA" :code "507"}
   {:label "Papua New Guinea" :value "PG" :code "675"}
   {:label "Paraguay" :value "PY" :code "595"}
   {:label "Peru" :value "PE" :code "51"}
   {:label "Philippines" :value "PH" :code "63"}
   {:label "Pitcairn" :value "PN" :code "64"}
   {:label "Poland" :value "PL" :code "48"}
   {:label "Portugal" :value "PT" :code "351"}
   {:label "Puerto Rico" :value "PR" :code "1-787"}
   {:label "Qatar" :value "QA" :code "974"}
   {:label "Reunion" :value "RE" :code "262"}
   {:label "Romania" :value "RO" :code "40"}
   {:label "Russia" :value "RU" :code "7"}
   {:label "Rwanda" :value "RW" :code "250"}
   {:label "Saint Barthelemy" :value "BL" :code "590"}
   {:label "Saint Helena" :value "SH" :code "290"}
   {:label "Saint Kitts and Nevis" :value "KN" :code "1-869"}
   {:label "Saint Lucia" :value "LC" :code "1-758"}
   {:label "Saint Martin" :value "MF" :code "590"}
   {:label "Saint Pierre and Miquelon" :value "PM" :code "508"}
   {:label "Saint Vincent and the Grenadines" :value "VC" :code "1-784"}
   {:label "Samoa" :value "WS" :code "685"}
   {:label "San Marino" :value "SM" :code "378"}
   {:label "Sao Tome and Principe" :value "ST" :code "239"}
   {:label "Saudi Arabia" :value "SA" :code "966"}
   {:label "Senegal" :value "SN" :code "221"}
   {:label "Serbia" :value "RS" :code "381"}
   {:label "Seychelles" :value "SC" :code "248"}
   {:label "Sierra Leone" :value "SL" :code "232"}
   {:label "Singapore" :value "SG" :code "65"}
   {:label "Sint Maarten" :value "SX" :code "1-721"}
   {:label "Slovakia" :value "SK" :code "421"}
   {:label "Slovenia" :value "SI" :code "386"}
   {:label "Solomon Islands" :value "SB" :code "677"}
   {:label "Somalia" :value "SO" :code "252"}
   {:label "South Africa" :value "ZA" :code "27"}
   {:label "South Korea" :value "KR" :code "82"}
   {:label "South Sudan" :value "SS" :code "211"}
   {:label "Spain" :value "ES" :code "34"}
   {:label "Sri Lanka" :value "LK" :code "94"}
   {:label "Sudan" :value "SD" :code "249"}
   {:label "Suriname" :value "SR" :code "597"}
   {:label "Svalbard and Jan Mayen" :value "SJ" :code "47"}
   {:label "Swaziland" :value "SZ" :code "268"}
   {:label "Sweden" :value "SE" :code "46"}
   {:label "Switzerland" :value "CH" :code "41"}
   {:label "Syria" :value "SY" :code "963"}
   {:label "Taiwan" :value "TW" :code "886"}
   {:label "Tajikistan" :value "TJ" :code "992"}
   {:label "Tanzania" :value "TZ" :code "255"}
   {:label "Thailand" :value "TH" :code "66"}
   {:label "Togo" :value "TG" :code "228"}
   {:label "Tokelau" :value "TK" :code "690"}
   {:label "Tonga" :value "TO" :code "676"}
   {:label "Trinidad and Tobago" :value "TT" :code "1-868"}
   {:label "Tunisia" :value "TN" :code "216"}
   {:label "Turkey" :value "TR" :code "90"}
   {:label "Turkmenistan" :value "TM" :code "993"}
   {:label "Turks and Caicos Islands" :value "TC" :code "1-649"}
   {:label "Tuvalu" :value "TV" :code "688"}
   {:label "U.S. Virgin Islands" :value "VI" :code "1-340"}
   {:label "Uganda" :value "UG" :code "256"}
   {:label "Ukraine" :value "UA" :code "380"}
   {:label "United Arab Emirates" :value "AE" :code "971"}
   {:label "United Kingdom" :value "GB" :code "44"}
   {:label "United States" :value "US" :code "1"}
   {:label "Uruguay" :value "UY" :code "598"}
   {:label "Uzbekistan" :value "UZ" :code "998"}
   {:label "Vanuatu" :value "VU" :code "678"}
   {:label "Vatican" :value "VA" :code "379"}
   {:label "Venezuela" :value "VE" :code "58"}
   {:label "Vietnam" :value "VN" :code "84"}
   {:label "Wallis and Futuna" :value "WF" :code "681"}
   {:label "Western Sahara" :value "EH" :code "212"}
   {:label "Yemen" :value "YE" :code "967"}
   {:label "Zambia" :value "ZM" :code "260"}
   {:label "Zimbabwe" :value "ZW" :code "263"}])

(defn country-combobox [{:keys [label value required class on-change]}]
  [combobox {:label label
             :value value
             :required required
             :class class
             :options countries
             :option-to-render (fn [{:keys [label value code]}]
                                 [:div
                                  [:img {:alt label
                                         :src (str "https://purecatamphetamine.github.io/country-flag-icons/3x2/" value ".svg")
                                         :class "inline mr-2 h-4 rounded-sm"}]
                                  (str label " +" code)])
             :on-change on-change}])
