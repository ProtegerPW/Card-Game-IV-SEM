package com.company;

import ui.controller.Controller;
import ui.view.Menu;

public class Game {
    public static void main(String[] args) {
        Player player = new Player();
        ClientSideConnection clientSideConnection = new ClientSideConnection(player);
        Menu menu = new Menu();
        Controller controller = new Controller(player, clientSideConnection, menu);
        controller.displayMenu();
    }
}