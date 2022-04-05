package ru.jcups.restapitask.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.jcups.restapitask.model.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ItemParser {


    /**todo
     * Add parsers for this pages:
     * https://www.lenovo.com/us/en/laptops/subseries-results?visibleDatas=991:Legion
     * https://www.hp.com/us-en/shop/vwa/laptops/segm=Home?jumpid=ma_lt_featured_na_6_210303
     * https://www.walmart.com/browse/electronics/gaming-laptops/3944_3951_1089430_1230091_1094888
     * https://www.asus.com/us/Laptops/For-Gaming/All-series/filter?Series=ROG-Republic-of-Gamers&SubSeries=ROG-Zephyrus,ROG-Flow,ROG-Strix
     * https://www.asus.com/us/Laptops/For-Gaming/All-series/filter?Series=TUF-Gaming
     */

    private static final Logger logger = LoggerFactory.getLogger(ItemParser.class);

    private static String model;
    private static String brand;
    private static String series;
    private static Map<String, Map<String, String>> mParams;

    public static Item parseItem(String uri) {
        logger.info("ItemParser.parseItem");
        logger.info("parseItem() called with: uri = [" + uri + "]");
        Document document = getDocument(uri);
        Item item = Item.builder()
//                .titleImageUrl(getTitleImageUrl(document))
                .title(getTitle(document))
//                .brand(brand)
//                .model(model)
//                .series(series)
                .price(getPrice(document))
//                .allImagesUrls(getImagesUrls(document))
//                .params(getParams(document))
                .description(getDescription())
                .build();
//        Set<String> images = item.getImages();
//        if (images.contains(item.getTitleImageUrl())) {
//            images.remove(item.getTitleImageUrl());
//            item.setAllImagesUrls(images);
//        }
        return item;
    }

    private static String getTitleImageUrl(Document document) {
        return document.getElementsByClass("spec-about__img").get(0)
                .attributes().get("src").replaceFirst("/[0-9]{3}/", "/big/");
    }

    private static String getTitle(Document document) {
        brand = "";
        series = "";
        model = "";
        String title = document.getElementsByClass("heading").get(0).child(0).text();
        brand = title.split(" ")[1];
        String s = title.substring(title.split(" ")[0].length() + brand.length() + 1).trim();
        String[] split = s.split(" ");
        if (split[split.length - 2].matches("[A-Z0-9-()]{4,}"))
            series = split[split.length - 2];
        if (split[split.length - 1].matches("[A-Z0-9-()]{4,}"))
            series = ((series != null ? series + " " : "") + split[split.length - 1]).trim();
        model = s.substring(0, s.length() - series.length()).trim();
        return title;
    }

    private static String getDescription() {
        Map<String, String> data = mParams.get("Общие данные");
        Map<String, String> display = mParams.get("Экран");
        Map<String, String> ram = mParams.get("Оперативная память");
        Map<String, String> hd = mParams.get("Жесткий диск");
        return "CPU: " + data.get("Тип процессора") + ", " + data.get("Код процессора") + "; \n" +
                "Screen: " + display.get("Размер") + ", " + display.get("Разрешение") + "; \n" +
                "RAM: " + ram.get("Размер оперативной памяти") + ", " + ram.get("Тип памяти") + "; \n" +
                "HDD: " + hd.get("Тип жесткого диска") + ", " + hd.get("Емкость (SSD)") + "; \n" +
                "GPU: " + mParams.get("Графика").get("Графический контроллер");
    }

    private static BigDecimal getPrice(Document document) {
        Elements elements = document.getElementsByClass("spec-about__price");
        if (elements.isEmpty()) {
            return new BigDecimal("3141.56");
        }
        Element element = elements.get(0);
        String text = element
                .text();
        String[] split = text.split("–");
        String replaceAll = split[0].replaceAll("[ a-zA-Zа-яА-Я.]", "");
        String replace = replaceAll
                .replace(",", ".");
        return new BigDecimal(replace);
    }

    private static Set<String> getImagesUrls(Document document) {
        Set<String> imagesUrls = new HashSet<>();
        for (Element element : document.getElementsByClass("spec-unit spec-images")) {
            for (Element el : element.getElementsByClass("spec-images__img")) {
                imagesUrls.add(el.attributes().get("src").replaceFirst("125", "big"));
            }
        }
        return imagesUrls;
    }

    private static Map<String, Map<String, String>> getParams(Document document) {
        mParams = null;
        Map<String, Map<String, String>> params = new LinkedHashMap<>();
        Elements elements = document.getElementsByClass("spec-unit");
        for (Element element : elements) {
            if (element.hasClass("spec-images")) continue;
            String key = element.getElementsByClass("spec-unit__ttl").text();
            Map<String, String> values = new LinkedHashMap<>();
            for (Element el : element.getElementsByClass("spec-list__it")) {
                values.put(el.getElementsByClass("spec-list__txt").text(),
                        el.getElementsByClass("spec-list__val").text());
            }
            params.put(key, values);
        }
        mParams = params;
        return params;
    }

    private static Document getDocument(String uri) {
        try {
            URL url = new URL(uri);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openConnection().getInputStream()));
            String inputLine;
            StringBuilder sb = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine).append('\n');
            in.close();
            return Jsoup.parse(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading document", e);
        }
    }
}
