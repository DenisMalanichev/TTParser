package com.company;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Выберете режим работы(парсинг описаний и ссылок(1) или поиск почт(2)");
        int ans = 1;
        try {
            ans = sc.nextInt();
        }catch (Exception e){
            System.out.println("Попробуйте еще раз и введите число");
            return;
        }
        sc.nextLine();
        String name = sc.nextLine();
        if(ans == 1){
            String url = "https://www.tiktok.com/@" + name + "?lang=ru-RU";
            System.out.println("Описание: ");
            String desk = grabDesk(url);
            if(!desk.equals("")){
                System.out.println(desk);
            }else{
                System.out.println("---");
            }
            System.out.println("Ссылки: ");
            String links = grabLinks(url);
            if(!links.equals("")){
                System.out.println(links);
            }else{
                System.out.println("---");
            }
        }else if(ans == 2){
            String url = "https://www.tiktok.com/@" + name + "?lang=ru-RU";
            try {
                System.out.println("Проверяется описание " + name + " - " + findEmails(grabDesk(url)));
                System.out.println("Проверяется ссылки " + name + " - " + findEmails(grabLinks(url)));
            }catch (Exception e){
                System.out.println("Ой, кажется такого пользователя нет");
            }
        }

    }

    public static String grabDesk(String url){
        try {
            Document doc = Jsoup.connect(url).get();
            Elements h2Elements = doc.select("h2.share-desc.mt10");
            String desk = "";
            desk = h2Elements.get(0).toString();
            boolean isOpenArrow = true;
            char arr[] = desk.toCharArray();
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
            //doc = Jsoup.connect("https://www.tiktok.com/search?q=" + searchRes + "&lang=ru-RU").get();
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
}
