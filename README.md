# AMT Altinn ACL

## Legge til testdata

For å legge til permanent testdata i cachen for å ikke gjøre oppslag mot Altinn så
kan man kjøre følgende SQL:

```sql
insert into rettigheter_cache(norsk_ident, rettigheter_json, expires_after)
values ('<fnr>',
        '{
          "version": 1,
          "rettigheter": [
            {
              "rettighetId": "9999999",
              "organisasjonsnummer": "<orgnr>"
            }
          ]
        }',
        to_timestamp('3000-01-01', 'YYYY-MM-DD'))
on conflict (norsk_ident)
    DO UPDATE SET rettigheter_json = EXCLUDED.rettigheter_json,
                  expires_after    = EXCLUDED.expires_after;
```