/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationproject;

/**
 *
 * @author ksyus
 */
public enum DealStatus {
    ZERO("Нет сделки"),
    NEW("Новый"),
    NEGOTIATION("Ведутся переговоры"),
    FULFILLMENT("Выполняются обязательства"),
    COMPLETED("Сделка завершена");

    private final String displayName;
    DealStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
