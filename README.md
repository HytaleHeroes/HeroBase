# HeroBase

Contains small customizations

For now only welcome messages.


Text format using [TinyMessage](https://github.com/Zoltus/TinyMessage):

<img width="278" height="174" alt="HytaleClient_2026-01-15_08-44-16" src="https://github.com/user-attachments/assets/04816490-0a9e-4554-977c-b08fd1baee4c" />

* **Gradients:** `<gradient:red:blue>Hello</gradient>` or multi-color `<gradient:gold:red:black>...`
* **Hex Colors:** `<color:#FF55FF>Custom Colors</color>` or `<color:red>Named Colors</color>`
* **Standard Styles:** `<b>Bold</b>`, `<i>Italic</i>`, `<u>Underline</u>`, `<mono>Monospace</mono>`
* **Clickable Links:** `<link:https://google.com>Click me!</link>`
* **Nested Styling:** Tags can be nested indefinitely.

---

# Config

`HytaleHeroes_HeroBase/HeroBase.json`

```json
{
  "WelcomeMessage": [
    "    <gradient:red:blue><b>Welcome to Hytale Heroes!</b></gradient>",
    " ",
    "    <i><link:https://discord.com/HytaleHeroes>CLICK HERE TO JOIN OUR COMMUNITY</link></i>",
    " ",
    "    Use /sc to claim land",
    "    Use /sethome to set a home"
  ],
  "WelcomeBackMessage": [
    "    <gradient:red:blue><b>Welcome back to Hytale Heroes, %player%!</b></gradient>",
    " ",
    "    <i><link:https://discord.com/HytaleHeroes>DID YOU JOIN OUR COMMUNITY ALREADY?</link></i>",
    " ",
    "    Use /sc to claim land",
    "    Use /sethome to set a home"
  ],
  "GlobalWelcomeMessage": "<gradient:red:blue><b>Welcome %player%!</b></gradient>",
  "GlobalWelcomeBackMessage": "<gradient:red:blue><b>Welcome back, %player%!</b></gradient>"
}
```

`GlobalWelcomeMessage` and `GlobalWelcomeBackMessage` will be sent to every other player online and can be left blank if you don't want those messages