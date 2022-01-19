import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Employee> list = parseXML("data.xml");
        System.out.println(list);
        String json = listToJson(list);
        writeString(json);
    }

    private static String listToJson(List<Employee> list) {
        // преобразуем List в строку
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String json) {
        // делаем красивый вывод
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);
        String prettyJsonString = gson.toJson(je);
        // записываем в файл
        try (FileWriter file = new FileWriter("data.json")) {
            file.write(prettyJsonString);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String fileNameXML) {
        List<Employee> list = new ArrayList<>();
        // Получение фабрики, чтобы после получить билдер документов.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            // Получили из фабрики билдер, который парсит XML, создает структуру Document
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            if (builder != null) {
                // Запарсили XML, создав структуру Document.
                // Теперь у нас есть доступ ко всем элементам, какие нам нужно
                doc = builder.parse(new File(fileNameXML));
            }
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        // корневой узел
        Node root = null;
        if (doc != null) {
            root = doc.getDocumentElement();
//            System.out.println("Корневой узел: " + root.getNodeName());
        }
        if (root != null) {
            read(root, list); // перебираем узлы внутри корневого
//            System.out.println("Закрытие корневого узла: " + root.getNodeName());
        }
        return list;
    }

    private static void read(Node node, List<Employee> list) {
        long idLong = 0;
        int ageInt = 0;
        String id, firstName, lastName, country, age;
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) { // перебираем все элементы корневого узла
            Node node_ = nodeList.item(i); // i-ый узел
            if (Node.ELEMENT_NODE == node_.getNodeType()) { // если тип узла (1) равен типу считываемого узла (get)
//                System.out.println("Текущий узел: " + node_.getNodeName()); // выводим его имя
                Element element = (Element) node_;

                // readTag - считываем значения тегов, они же аттрибуты
                id = readTag("id", element);
                if (id != null) {
                    idLong = Long.parseLong(id);
                }
                firstName = readTag("firstName", element);
                lastName = readTag("lastName", element);
                country = readTag("country", element);
                age = readTag("age", element);
                if (age != null) {
                    ageInt = Integer.parseInt(age);
                }

                if (id != null && firstName != null && lastName != null && country != null && age != null) {
                    list.add(new Employee(idLong, firstName, lastName, country, ageInt));
                }
                read(node_, list);
            }
        }
    }

    private static String readTag(String nameTag, Element e) {
        String s = null;
        NodeList list = e.getElementsByTagName(nameTag);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();
            if (subList != null && subList.getLength() > 0) {
                s = subList.item(0).getNodeValue();
            }
        }
        return s;
    }
}