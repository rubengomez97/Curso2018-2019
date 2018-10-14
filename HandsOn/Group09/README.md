Gianfranco Pérez Canales - Gianpc23
Sergio González Martín - DarckRevan
Junjie Li - jjlii
Alejandro Cobo Cabornero - Alejandro-Cobo
Ignacio Ballesteros González - ignaciobll

# License

This datasets can be used for commercial and non-commercial activities.

- We must cite our sources.
- We must specify the date of the last update in the dataset used.

Further description and full conditions are available at:

https://datos.madrid.es/portal/site/egob/menuitem.3efdb29b813ad8241e830cc2a8a409a0/?vgnextoid=108804d4aab90410VgnVCM100000171f5a0aRCRD&vgnextchannel=b4c412b9ace9f310VgnVCM100000171f5a0aRCRD&vgnextfmt=default


# Dataset analysis

## Noise Pollution

| ColName   | Type    | Example Value      | Comment                          |
|-----------|---------|--------------------|----------------------------------|
| Fecha     | string  | ene-17             | Date                             |
| NMT       | integer | 1                  | Measurement Station Name         |
| Situación | string  | Paseo de Recoletos | Station location, maybe street.  |
| Ld        | integer | 69.1               |                                  |
| Le        | integer | 69.2               |                                  |
| Ln        | integer | 66.5               |                                  |
| LAeq24    | integer | 68.2               | Constant noise level             |
| LAS01     | integer | 74                 | Top noise level over 1% of time  |
| LAS10     | integer | 71.3               | Top noise level over 10% of time |
| LAS50     | integer | 65.8               | Top noise level over 50% of time |
| LAS90     | integer | 57.9               | Top noise level over 90% of time |
| LAS99     | integer | 50                 | Top noise level over 99% of time |

## Air Pollution

| ColName        | Type    | Example Value | Comment                     |
|----------------|---------|---------------|-----------------------------|
| PROVINCIA      | integer | 28            | Province                    |
| MUNICIPIO      | integer | 079           | District                    |
| ESTACION       | integer | 4             | Station Name                |
| MAGNITUD       | integer | 1             | Magnitude                   |
| PUNTO_MUESTREO | string  | 28079004_1_38 |                             |
| ANO            | integer | 2017          | Year                        |
| MES            | integer | 12            | Month                       |
| DIA            | integer | 01            | Day                         |
| H{1..24}       | integer | 00001         | Value at hour X             |
| V{1..24}       | char    | V             | Value notation. Allways "V" |

https://datos.madrid.es/FWProjects/egob/Catalogo/MedioAmbiente/Aire/Ficheros/Interprete_ficheros_%20calidad_%20del_%20aire_global_.pdf

# Resource Naming Strategy

| Name                      | URI                                                              |
|---------------------------|------------------------------------------------------------------|
| Domain                    | `http://group09.data/                                          ` |
| Ontological terms path    | `http://group09.data/ontology/Pollution#                       ` |
| Individual paths          | `http://group09.data/resources/                                ` |
| Ontological terms pattern | `http://group09.data/ontology/Pollution#<term-name>            ` |
| Indivudual patter         | `http://group09.data/resources/<resource-type>/<resource-name> ` |
