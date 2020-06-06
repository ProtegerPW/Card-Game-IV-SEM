package com.company;

import ui.controller.Controller;
import ui.view.Menu;

public class Game {
    private static Player player;
    private static ClientSideConnection clientSideConnection;
    private static Menu menu;
    private static Controller controller;

    public static void main(String[] args) {
        player = new Player();
        clientSideConnection = new ClientSideConnection(player);
        menu = new Menu();
        controller = new Controller(player, clientSideConnection, menu);
        controller.displayMenu();
    }
}