package gg.hytaleheroes.herobase.extra.navigator;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.List;

public class NavigatorConfig {
    public List<NavigatorPage.Section> sections = List.of(
            new NavigatorPage.Section("Navigation", List.of(
                    new NavigatorPage.Card("Hub", "Teleport to the Hub", "hub", "UI/Custom/Pages/Navigator/Icons/target.png", false),
                    new NavigatorPage.Card("Home", "Open your Homes list", "home", "UI/Custom/Pages/Navigator/Icons/home.png", false),
                    new NavigatorPage.Card("Teleport Request", "Send a teleport request", "tpa", "UI/Custom/Pages/Navigator/Icons/map-marker.png", false),
                    new NavigatorPage.Card("Random Teleport", "Random Teleport", "rtp", "UI/Custom/Pages/Navigator/Icons/portal.png", false),
                    new NavigatorPage.Card("Warps", "Server Warps", "warp", "UI/Custom/Pages/Navigator/Icons/void.png", false)
            )),

            new NavigatorPage.Section("Combat", List.of(
                    new NavigatorPage.Card("FFA Arena", "Free For All PVP Arena", "arena", "UI/Custom/Pages/Navigator/Icons/battle.png", true),
                    new NavigatorPage.Card("Ranked Match", "Coming Soon!", "", "UI/Custom/Pages/Navigator/Icons/battleaxe.png", true),
                    new NavigatorPage.Card("Leaderboards", "Global Leaderboards", "leaderboard", "UI/Custom/Pages/Navigator/Icons/trophy.png", false)
            )),

            new NavigatorPage.Section("Player", List.of(
                    new NavigatorPage.Card("Abilities", "Coming Soon!", "", "UI/Custom/Pages/Navigator/Icons/lightning.png", false),
                    new NavigatorPage.Card("Profile", "Open your profile", "profile", "UI/Custom/Pages/Navigator/Icons/user.png", false),
                    new NavigatorPage.Card("Market", "Open the Market", "market", "UI/Custom/Pages/Navigator/Icons/pouch.png", false),
                    new NavigatorPage.Card("Cosmetics", "Coming Soon!", "", "UI/Custom/Pages/Navigator/Icons/armor.png", false)
            )),

            new NavigatorPage.Section("Manage", List.of(
                    new NavigatorPage.Card("Claim", "Claim an area", "claim", "UI/Custom/Pages/Navigator/Icons/padlock-locked.png", false),
                    new NavigatorPage.Card("Party", "Manage your party", "party", "UI/Custom/Pages/Navigator/Icons/user.png", false),
                    new NavigatorPage.Card("Settings", "Coming Soon!", "", "UI/Custom/Pages/Navigator/Icons/settings.png", false)
            )),

            new NavigatorPage.Section("Information", List.of(
                    new NavigatorPage.Card("Help", "Command info", "help", "UI/Custom/Pages/Navigator/Icons/misc.png", false),
                    new NavigatorPage.Card("Discord", "Get a link to our Discord", "discord", "UI/Custom/Pages/Navigator/Icons/chat.png", false),
                    new NavigatorPage.Card("Vote", "Get rewards and Support us by Voting for our server!", "vote", "UI/Custom/Pages/Navigator/Icons/paper.png", false),
                    new NavigatorPage.Card("TikTok", "Coming Soon!", "", "UI/Custom/Pages/Navigator/Icons/hand-thumbs-up.png", false)
            ))
    );
    public static final BuilderCodec<NavigatorPage.Card> CARD_CODEC =
            BuilderCodec.builder(NavigatorPage.Card.class, NavigatorPage.Card::new)

                    .append(new KeyedCodec<>("Name", Codec.STRING),
                            (c, v) -> c.name = v,
                            c -> c.name).add()

                    .append(new KeyedCodec<>("Tooltip", Codec.STRING),
                            (c, v) -> c.tooltip = v,
                            c -> c.tooltip).add()

                    .append(new KeyedCodec<>("Command", Codec.STRING),
                            (c, v) -> c.command = v,
                            c -> c.command).add()

                    .append(new KeyedCodec<>("Icon", Codec.STRING),
                            (c, v) -> c.icon = v,
                            c -> c.icon).add()

                    .build();

    public static final BuilderCodec<NavigatorPage.Section> SECTION_CODEC =
            BuilderCodec.builder(NavigatorPage.Section.class, NavigatorPage.Section::new)

                    .append(new KeyedCodec<>("Name", Codec.STRING),
                            (s, v) -> s.name = v,
                            s -> s.name).add()

                    .append(
                            new KeyedCodec<>("Cards",
                                    new ArrayCodec<>(CARD_CODEC, NavigatorPage.Card[]::new)),
                            (s, v) -> s.cards = List.of(v),
                            s -> s.cards.toArray(NavigatorPage.Card[]::new)
                    ).add()

                    .build();

    public static final BuilderCodec<NavigatorConfig> CODEC =
            BuilderCodec.builder(NavigatorConfig.class, NavigatorConfig::new)

                    .append(
                            new KeyedCodec<>("Sections",
                                    new ArrayCodec<>(SECTION_CODEC, NavigatorPage.Section[]::new)),
                            (c, v) -> c.sections = List.of(v),
                            c -> c.sections.toArray(NavigatorPage.Section[]::new)
                    ).add()

                    .build();
}