# Poznavacka is a simple program for learning names of plants or animals and theirs taxonomy

You can set up config file which example is in src directory.
This file should be place on Windows in user_home/AppData/Roaming/poznavacka/config and on Linux $HOME/.config/poznavacka/config.
Its tested only on Linux and case for mac isn't implemented.
Directory poznavacka and file config must be created by user.

Program loads pictures from directory (and subdirectories), and each picture has to follow same naming scheme.

e.g. houba pletená (KM.živočišné houby,houbovci;TR.křemičití) 2.jpg

If you don't need taxonomy you can just exclude it.

name_of_organism (taxonomy) number_of_picure.jpg/png

taxonomy is written in in form CATEGORY_ABBREVIATION.name_of_category_for_specific_organism;SUBCATEGORY_ABBREVIATION.name of subcategory etc.

If category has multiple names, or you want to be able to use both English and Latin category names, you have to separate different variants with comma.

Program itself can be controlled with keyboard shortcuts, these are explained in hlep prompt, which you may access via H button.


## Taxonomy abbreviations

These abbreviations are hardcoded in FXMLDocumtntContoller.
Default are Czech for English are commented.
You may also edit them, but both array must have same length.
These also don't have to contain taxonomy, you may also used them for different information, like if animal is carnivore, able to do Parthenogenesis or what ever you want to test.

### Czech
+ KM - kmen
+ PK - podkmen
+ NT - nadtřída
+ TR - třída
+ PT - podtřída
+ RA - řád

+ příklad -- vodoměrka (KM.členovci;PK.vzdušnicovci;NT.šestinozí;TR.hmyz;PT.křídlatí;RA.ploštice) 2.jpg
	+ vodoměrka je název živočicha
	+ členovci je kmen živočicha
	+ vzdušnicovci je podkmen
	+ šestinozí je nadtřída
	+ hmyz je třída
	+ křídlatí je podtřída
	+ ploštice je řád (technicky chyba, ale řád nemá český název, uveden podřád)
	+ 2 je číslo obrázku živočicha, teoreticky zde může být cokoliv a program to bude ignorovat

### English
+ PL - phylum
+ SP - subphylum
+ DV - division
+ CL - class
+ SL - subclass
+ OD - order
+ maybe English taxonomy is wrong, don't really care since I'm using Czech

+ example -- vodoměrka (PL.členovci;SP.vzdušnicovci;DV.šestinozí;CL.hmyz;SL.křídlatí;OD.ploštice) 2.jpg
	+ vodoměrka is organism name
	+ členovci is phylum
	+ vzdušnicovci is subphylum
	+ šestinozí is division
	+ hmyz is class
	+ křídlatí is subclass
	+ ploštice is order
	+ 2 number of picture, as I have multiple pictures for one organism

# Directory structure

Program will go recursively through sub directories.
Directory names don't affect program at all, but they should contain only images.

Example of directory structure.

```
Poznávačka/
├── Členovci
│   ├── Klepítkatci
│   ├── Korýši
│   └── Vzdušnicovci
│       ├── Mnohonozí
│       └── Šestinozí
├── Hlístice
├── Kroužkovci
├── Měkkýši
│   ├── Chroustnatky
│   ├── Hlavonožci
│   ├── Mlži
│   └── Plži
├── Ostnokožci
├── Ploštěnci
├── Strunatci
│   ├── Bezlebeční
│   ├── Obratlovci
│   │   ├── Bezčelistnatci
│   │   └── Čelistnatci
│   │       ├── Nozdratí
│   │       ├── Obojživelníci
│   │       ├── Paprskoploutví
│   │       ├── Paryby
│   │       ├── Plazi
│   │       ├── Ptáci
│   │       └── Savci
│   └── Pláštěnci
├── Vířníci
├── Žahavci
└── Živočišné houby
```
