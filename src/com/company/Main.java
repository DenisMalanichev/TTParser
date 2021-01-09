package com.company;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);
        System.out.println("Введите путь, где будет создане файл с результатами");
        String pass  = sc.nextLine();
        Date date = new Date();
        String fileName = "TTparserRes" + date.getTime() + ".txt";

            File resFile = new File(pass + "/" + fileName);
        try {
                resFile.createNewFile();
        }catch (Exception e){
            System.out.println("Путь неверный");
        }

        System.out.println("Выберете режим работы(парсинг описаний и ссылок(1) или поиск почт(2)");
        int ans;
        try {
            ans = sc.nextInt();
        }catch (Exception e){
            System.out.println("Попробуйте еще раз и введите число");
            return;
        }
        System.out.println("Хотите ли вы брать имена из файла?(1) - да, (2) - нет");
        int isFile;
        try {
            isFile = sc.nextInt();
        }catch (Exception e){
            System.out.println("Попробуйте еще раз и введите число");
            return;
        }
        if(isFile == 1){
            System.out.println("Введите путь до файла:");
            ArrayList<String> names = null;
            sc.nextLine();
            try {
                names = readDataFromFile(sc.nextLine());
            }catch (Exception e){
                System.out.println("Кажется, такого файла нет");
                return;
            }
            for(int i =0; i<names.size(); i++){
                if(ans == 1){
                    mode1(names.get(i));
                }else if(ans == 2){
                    mode2(names.get(i), fileName);
                }
            }
        }else if(isFile == 2) {
            System.out.println("Введите имя аккаунта");
            sc.nextLine();
            String name = sc.nextLine();
            if (ans == 1) {
               mode1(name);
            } else if (ans == 2) {
                mode2(name, fileName);
            }else{
                System.out.println("Число должно было быть 1 или 2");
            }
        }else{
            System.out.println("Число должно было быть 1 или 2");
        }
    }

    public static String grabDesc(String url){
        try {
            Document doc = Jsoup.connect(url).get();
            Elements h2Elements = doc.select("h2.share-desc.mt10");
            String desc = "";
            desc = h2Elements.get(0).toString();
            boolean isOpenArrow = true;
            char arr[] = desc.toCharArray();
            String output = "";
            for(int i =0; i<arr.length; i++){
                if(arr[i] == '<'){
                    isOpenArrow = true;
                }
                if(!isOpenArrow){
                    output += arr[i];
                }
                if(arr[i] == '>'){
                    isOpenArrow = false;
                }
            }
            return output;
        } catch (IOException e) {
            return null;
        }
    }
    public static String grabLinks(String url){
        try {
            Document doc = Jsoup.connect(url).get();
            Elements h2Elements = doc.select("div.share-links");
            String links = h2Elements.get(0).toString();
            boolean isOpenArrow = true;
            char arr[] = links.toCharArray();
            String output = "";
            for(int i =0; i<arr.length; i++){
                if(arr[i] == '<'){
                    isOpenArrow = true;
                }
                if(!isOpenArrow){
                    output += arr[i];
                }
                if(arr[i] == '>'){
                    isOpenArrow = false;
                }
            }
            return output;
        }catch (Exception e){
            return null;
        }

    }
    public static ArrayList<String> parsNames(String searchRes){
        ArrayList<String> namesArr = new ArrayList<>();

        try {
            WebClient webClient = new WebClient();
            HtmlPage page = webClient.getPage(new File("https://www.tiktok.com/tag/pets?lang=en").toURI().toURL());
            Document doc = Jsoup.parse(page.asXml());
            System.out.println(doc.html());
            Elements h2Elements = doc.select("div.jsx-2261688415");
            for(Element el: h2Elements){
                namesArr.add(el.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       return namesArr;
    }

    public static String findEmails(String line){
        Pattern pattern = Pattern.compile("([a-z0-9_.-]+)@([a-z0-9_.-]+[a-z])");
        Matcher matcher = pattern.matcher(line);
        String output = "";
        while(matcher.find()){
          output += matcher.group();
        }
        return output;
    }

    public static ArrayList<String> readDataFromFile(String fileLocation) throws Exception{
          ArrayList<String> names = new ArrayList<>();
          File file = new File(fileLocation);
          Scanner sc = new Scanner(file);
          while(sc.hasNextLine()){
              names.add(sc.nextLine());
          }
           return names;
    }
    public static void mode1(String name){
        String url = "https://www.tiktok.com/@" + name + "?lang=ru-RU";
        System.out.println("Описание: ");
        String desc = grabDesc(url);
        if (!desc.equals("")) {
            System.out.println(desc);
        } else {
            System.out.println("---");
        }
        System.out.println("Ссылки: ");
        String links = grabLinks(url);
        if (!links.equals("")) {
            System.out.println(links);

        } else {
            System.out.println("---");
        }
    }

    public static void mode2(String name, String fileName){
        String url = "https://www.tiktok.com/@" + name + "?lang=ru-RU";
        try {
            System.out.println("Проверяется описание " + name + " - " + findEmails(grabDesc(url)));
            System.out.println("Проверяется ссылки " + name + " - " + findEmails(grabLinks(url)));
            String mail = findEmails(grabDesc(url));
            if(mail.equals("")){
                mail = findEmails(grabLinks(url));
            }
            if(!mail.equals("")) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);
                    fileOutputStream.write(mail.getBytes());
                    fileOutputStream.write('\n');
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Ой, кажется такого пользователя нет");
        }
    }


}
