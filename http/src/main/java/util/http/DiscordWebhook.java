// https://gist.github.com/k3kdude/fba6f6b37594eae3d6f9475330733bdb

package util.http;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import util.Chain;
import util.Validate;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class DiscordWebhook implements Chain<DiscordWebhook> {
    public static @NotNull DiscordWebhook of(@NotNull String url, @Nullable String username, @NotNull String content) {
        Validate.notNull(url, "url cannot be null");
        Validate.notNull(content, "content cannot be null");
        return new DiscordWebhook(url)
                .setUsername(username)
                .setContent(content);
    }

    public static @NotNull DiscordWebhook of(@NotNull String url, @Nullable String username, @Nullable String title, @Nullable String description, @Nullable Color color) {
        Validate.notNull(url, "url cannot be null");
        return new DiscordWebhook(url)
                .setUsername(username)
                .addEmbed(new EmbedObject().setTitle(title).setDescription(description).setColor(color));
    }

    @Getter private final String url;
    @Getter @Setter @Accessors(chain = true) private String content;
    @Getter @Setter @Accessors(chain = true) private String username;
    @Getter @Setter @Accessors(chain = true) private String avatarUrl;
    @Getter @Setter @Accessors(chain = true) private boolean tts;
    private final List<EmbedObject> embeds = new ArrayList<>();

    /**
     * Constructs a new DiscordWebhook instance
     *
     * @param url The webhook URL obtained in Discord
     */
    public DiscordWebhook(@NonNull String url) { this.url = url; }

    @NotNull
    public DiscordWebhook addEmbed(@NonNull EmbedObject embed) {
        this.embeds.add(embed);
        return this;
    }

    public void execute() throws IOException { execute(null); }

    /**
     * Executes a webhook. Consumer will be invoked once before the json data is written into OutputStream.
     * @param action the consumer to run
     */
    public void execute(@Nullable BiConsumer<JSONObject, HttpsURLConnection> action) throws IOException {
        if (this.content == null && this.embeds.isEmpty()) throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        JSONObject json = new JSONObject();
        json.put("content", this.content);
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", this.tts);
        if (!this.embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();
            for (EmbedObject embed : this.embeds) {
                JSONObject jsonEmbed = new JSONObject();
                jsonEmbed.put("title", embed.getTitle());
                jsonEmbed.put("description", embed.getDescription());
                jsonEmbed.put("url", embed.getUrl());
                if (embed.getColor() != null) {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();
                    jsonEmbed.put("color", rgb);
                }
                EmbedObject.Footer footer = embed.getFooter();
                EmbedObject.Image image = embed.getImage();
                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                EmbedObject.Author author = embed.getAuthor();
                List<EmbedObject.Field> fields = embed.getFields();
                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();
                    jsonFooter.put("text", footer.getText());
                    jsonFooter.put("icon_url", footer.getIconUrl());
                    jsonEmbed.put("footer", jsonFooter);
                }
                if (image != null) {
                    JSONObject jsonImage = new JSONObject();
                    jsonImage.put("url", image.getUrl());
                    jsonEmbed.put("image", jsonImage);
                }
                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();
                    jsonThumbnail.put("url", thumbnail.getUrl());
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }
                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();
                    jsonAuthor.put("name", author.getName());
                    jsonAuthor.put("url", author.getUrl());
                    jsonAuthor.put("icon_url", author.getIconUrl());
                    jsonEmbed.put("author", jsonAuthor);
                }
                List<JSONObject> jsonFields = new ArrayList<>();
                for (EmbedObject.Field field : fields) {
                    JSONObject jsonField = new JSONObject();
                    jsonField.put("name", field.getName());
                    jsonField.put("value", field.getValue());
                    jsonField.put("inline", field.isInline());
                    jsonFields.add(jsonField);
                }
                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }
            json.put("embeds", embedObjects.toArray());
        }
        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "acrylic-style/java-util @ @version@");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        if (action != null) action.accept(json, connection);
        OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();
        connection.getInputStream().close();
        connection.disconnect();
    }

    public static class EmbedObject {
        @Getter @Setter @Accessors(chain = true) private String title;
        @Getter @Setter @Accessors(chain = true) private String description;
        @Getter @Setter @Accessors(chain = true) private String url;
        @Getter @Setter @Accessors(chain = true) private Color color;
        @Getter @Setter @Accessors(chain = true) private Footer footer;
        @Getter @Setter @Accessors(chain = true) private Thumbnail thumbnail;
        @Getter @Setter @Accessors(chain = true) private Image image;
        @Getter private Author author;
        @Getter private final List<Field> fields = new ArrayList<>();

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Footer {
            @Getter private final String text;
            @Getter private final String iconUrl;
        }

        @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Thumbnail {
            @Getter private final String url;
        }

        @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Image {
            @Getter(AccessLevel.PRIVATE) public final String url;
        }

        @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Author {
            @Getter private final String name;
            @Getter private final String url;
            @Getter private final String iconUrl;
        }

        @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Field {
            @Getter private final String name;
            @Getter private final String value;
            @Getter private final boolean inline;
        }
    }
}
