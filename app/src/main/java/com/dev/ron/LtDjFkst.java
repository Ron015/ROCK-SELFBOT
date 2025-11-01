package com.dev.ron;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.text.TextWatcher;
import android.text.Editable;

public class LtDjFkst extends Fragment {

    private LinearLayout commandsListContainer;
    private SharedPreferences sharedPreferences;
    private EditText cmdsearchBar;
    private TextWatcher searchWatcher;
    public LtDjFkst() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leulehr, container, false);
        commandsListContainer = view.findViewById(R.id.commandsListContainer);
        sharedPreferences = requireContext().getSharedPreferences("Commands", Context.MODE_PRIVATE);

        loadCommands();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get activity's search bar
        if (getActivity() != null) {
            cmdsearchBar = ((ZkUdYs) getActivity()).findViewById(R.id.search_input);
            
            // Create text watcher
            searchWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    loadCommands(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            };
            cmdsearchBar.addTextChangedListener(searchWatcher);
        }
    }
    private void loadCommands() {
        loadCommands("");
    }

    private void loadCommands(String filter) {
        commandsListContainer.removeAllViews();

        String json = sharedPreferences.getString("commands", null);
        if (json == null) {
            json = "{\n" +
            "  \"help\": \"Show list of Commands and uses\",\n" +
            "  \"advice\": \"Get random advice\",\n" +
            "  \"afk <ON|OFF> [message]\": \"Toggle AFK status\",\n" +
            "  \"airplane\": \"9/11 animation\",\n" +
            "  \"anime <query>\": \"Search anime info\",\n" +
            "  \"ascii <message>\": \"Convert text to ASCII art\",\n" +
            "  \"autoreply <ON|OFF>\": \"Toggle auto-reply to DMs\",\n" +
            "  \"banall\": \"Ban all server members (dangerous)\",\n" +
            "  \"birthday\": \"Set your birthday\",\n" +
            "  \"birthdaylist\": \"Show server birthdays\",\n" +
            "  \"btcprice\": \"Check Bitcoin price\",\n" +
            "  \"changeprefix <prefix>\": \"Change bot prefix\",\n" +
            "  \"clear\": \"Clear channel messages\",\n" +
            "  \"cleardm <amount>\": \"Clear your DMs\",\n" +
            "  \"closealldm\": \"Close All DMs\",\n" +
            "  \"convert <value> <from> <to>\": \"Convert units/currency\",\n" +
            "  \"copycat <ON|OFF> <@user>\": \"Copy user's messages\",\n" +
            "  \"crypto <coin>\": \"Get crypto price\",\n" +
            "  \"deleteallguilds\": \"Delete All servers in which you are owner\",\n" +
            "  \"dick [@user]\": \"Dick size joke\",\n" +
            "  \"dominfo <domain>\": \"Get domain info\",\n" +
            "  \"editsnipe\": \"Show last edited message\",\n" +
            "  \"eightball <question>\": \"Magic 8-ball\",\n" +
            "  \"embed <text>\": \"Create embedded message\",\n" +
            "  \"fact\": \"Get random fact\",\n" +
            "  \"fakehack <@user>\": \"Fake hack prank\",\n" +
            "  \"fetchmembers\": \"List server members\",\n" +
            "  \"firstmessage\": \"Get first message in channel\",\n" +
            "  \"flip\": \"Flip a coin\",\n" +
            "  \"geoip <ip>\": \"IP geolocation lookup\",\n" +
            "  \"github <username>\": \"Get GitHub user info\",\n" +
            "  \"gentoken\": \"Generate fake Discord token\",\n" +
            "  \"guildbanner\": \"Get server banner\",\n" +
            "  \"guildicon\": \"Get server icon\",\n" +
            "  \"guildinfo\": \"Get server info\",\n" +
            "  \"guildrename <name>\": \"Rename server\",\n" +
            "  \"hypesquad <house>\": \"Change HypeSquad house\",\n" +
            "  \"hidemention <visible> <hidden>\": \"Hide @mentions\",\n" +
            "  \"join <invite>\": \"Join server via invite\",\n" +
            "  \"joke\": \"Tell a joke\",\n" +
            "  \"kickall\": \"Kick all server members (dangerous)\",\n" +
            "  \"leaveallgroup\": \"Leave all dm groups\",\n" +
            "  \"leaveallguild\": \"Leave All servers in which owner not you\",\n" +
            "  \"leetspeak <text>\": \"Convert to leet speak\",\n" +
            "  \"listcommands\": \"List all commands\",\n" +
            "  \"love <name1> <name2>\": \"Calculate love percentage\",\n" +
            "  \"massrename <name>\": \"Mass rename members\",\n" +
            "  \"massrenamechannels <name>\": \"Mass rename channels\",\n" +
            "  \"meme\": \"Get random meme\",\n" +
            "  \"minesweeper <size>\": \"Generate minesweeper game\",\n" +
            "  \"nitro\": \"Generate fake nitro link\",\n" +
            "  \"npm <package>\": \"Search NPM packages\",\n" +
            "  \"ping\": \"Check bot latency\",\n" +
            "  \"pingweb <url>\": \"Ping website\",\n" +
            "  \"playing <text>\": \"Set playing status\",\n" +
            "  \"pokedex <pokemon>\": \"Get Pokémon info\",\n" +
            "  \"purge <amount>\": \"Bulk delete messages\",\n" +
            "  \"quotes\": \"Get random quote\",\n" +
            "  \"quickdelete <message>\": \"Send self-destructing message\",\n" +
            "  \"rate <thing>\": \"Rate something 1-10\",\n" +
            "  \"remoteuser <ADD|REMOVE> <@user>\": \"Manage remote users\",\n" +
            "  \"reverse <text>\": \"Reverse text\",\n" +
            "  \"roll [max]\": \"Roll random number\",\n" +
            "  \"say <message>\": \"Make bot say something\",\n" +
            "  \"selfban\": \"Ban yourself from server\",\n" +
            "  \"sendall <message>\": \"Send message to all channels\",\n" +
            "  \"snipe\": \"Show last deleted message\",\n" +
            "  \"spam <amount> <message>\": \"Spam messages\",\n" +
            "  \"streaming <text>\": \"Set streaming status\",\n" +
            "  \"stopactivity\": \"Clear your status\",\n" +
            "  \"teste\": \"Test command\",\n" +
            "  \"tokeninfo <token>\": \"Get token info\",\n" +
            "  \"tts <text>\": \"Text-to-speech\",\n" +
            "  \"uptime\": \"Show bot uptime\",\n" +
            "  \"usericon <@user>\": \"Get user avatar\",\n" +
            "  \"userinfo <@user>\": \"Get user info\",\n" +
            "  \"webhook spam\": \"Manage webhooks\",\n" +
            "  \"whremove <url>\": \"Delete webhook\",\n" +
            "  \"wiki <query>\": \"Search Wikipedia\",\n" +
            "  \"neko\": \"Sends a fun neko response\",\n" +
            "  \"ahegao\": \"Sends an NSFW ahegao image (⚠️ Use responsibly)\",\n" +
            "  \"anal\": \"Sends an NSFW anal image (⚠️ Use responsibly)\",\n" +
            "  \"ass\": \"Sends an NSFW ass image (⚠️ Use responsibly)\",\n" +
            "  \"bj\": \"Sends an NSFW bj image (⚠️ Use responsibly)\",\n" +
            "  \"blowjob\": \"Sends an NSFW blowjob image (⚠️ Use responsibly)\",\n" +
            "  \"boobs\": \"Sends an NSFW boobs image (⚠️ Use responsibly)\",\n" +
            "  \"boobs_gif\": \"Sends an NSFW boobs_gif image (⚠️ Use responsibly)\",\n" +
            "  \"classic\": \"Sends an NSFW classic image (⚠️ Use responsibly)\",\n" +
            "  \"cuddle\": \"Sends an NSFW cuddle image (⚠️ Use responsibly)\",\n" +
            "  \"cum\": \"Sends an NSFW cum image (⚠️ Use responsibly)\",\n" +
            "  \"ero\": \"Sends an NSFW ero image (⚠️ Use responsibly)\",\n" +
            "  \"feet\": \"Sends an NSFW feet image (⚠️ Use responsibly)\",\n" +
            "  \"feetg\": \"Sends an NSFW feetg image (⚠️ Use responsibly)\",\n" +
            "  \"femdom\": \"Sends an NSFW femdom image (⚠️ Use responsibly)\",\n" +
            "  \"hentai\": \"Sends an NSFW hentai image (⚠️ Use responsibly)\",\n" +
            "  \"holo\": \"Sends an NSFW holo image (⚠️ Use responsibly)\",\n" +
            "  \"hololewd\": \"Sends an NSFW hololewd image (⚠️ Use responsibly)\",\n" +
            "  \"kemonomimi\": \"Sends an NSFW kemonomimi image (⚠️ Use responsibly)\",\n" +
            "  \"keta\": \"Sends an NSFW keta image (⚠️ Use responsibly)\",\n" +
            "  \"kuni\": \"Sends an NSFW kuni image (⚠️ Use responsibly)\",\n" +
            "  \"les\": \"Sends an NSFW les image (⚠️ Use responsibly)\",\n" +
            "  \"lewd\": \"Sends an NSFW lewd image (⚠️ Use responsibly)\",\n" +
            "  \"ngif\": \"Sends an NSFW ngif image (⚠️ Use responsibly)\",\n" +
            "  \"nsfw_neko_gif\": \"Sends an NSFW nsfw_neko_gif image (⚠️ Use responsibly)\",\n" +
            "  \"panties\": \"Sends an NSFW panties image (⚠️ Use responsibly)\",\n" +
            "  \"pussy\": \"Sends an NSFW pussy image (⚠️ Use responsibly)\",\n" +
            "  \"solo\": \"Sends an NSFW solo image (⚠️ Use responsibly)\",\n" +
            "  \"spank\": \"Sends an NSFW spank image (⚠️ Use responsibly)\",\n" +
            "  \"thigh\": \"Sends an NSFW thigh image (⚠️ Use responsibly)\",\n" +
            "  \"tits\": \"Sends an NSFW tits image (⚠️ Use responsibly)\",\n" +
            "  \"trap\": \"Sends an NSFW trap image (⚠️ Use responsibly)\",\n" +
            "  \"waifu\": \"Sends an NSFW waifu image (⚠️ Use responsibly)\",\n" +
            "  \"yuri\": \"Sends an NSFW yuri image (⚠️ Use responsibly)\"\n" +
            "}";
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String command = keys.next();
                String description = jsonObject.getString(command);
                String allData = (command + " " + description).toLowerCase();
                if (allData.contains(filter.toLowerCase())) {
                    addCommandView(command, description);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addCommandView(String command, String description) {
        Context context = requireContext();

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundResource(R.drawable.rounded_dropdown_bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 32);
        layout.setLayoutParams(params);

        TextView title = new TextView(context);
        title.setText(command);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(android.graphics.Color.WHITE);
        title.setPadding(0, 0, 0, 10);
        layout.addView(title);

        final TextView descView = new TextView(context);
        descView.setText(description);
        descView.setTextColor(android.graphics.Color.WHITE);
        descView.setVisibility(View.GONE);
        descView.setAlpha(0f);
        layout.addView(descView);

        title.setOnClickListener(v -> {
            if (descView.getVisibility() == View.GONE) {
                descView.setVisibility(View.VISIBLE);
                descView.animate().alpha(1f).setDuration(300).start();
            } else {
                descView.animate().alpha(0f).setDuration(300).withEndAction(() -> descView.setVisibility(View.GONE)).start();
            }
        });

        commandsListContainer.addView(layout);
    }
    @Override
    public void onPause() {
        super.onPause();
        // Unregister watcher when fragment is not visible
        if (cmdsearchBar != null) {
            cmdsearchBar.removeTextChangedListener(searchWatcher);
        }
    }
}