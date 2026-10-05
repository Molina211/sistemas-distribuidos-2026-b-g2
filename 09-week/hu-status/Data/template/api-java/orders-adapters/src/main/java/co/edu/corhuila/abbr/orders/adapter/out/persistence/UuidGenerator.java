package co.edu.corhuila.abbr.orders.adapter.out.persistence;

import co.edu.corhuila.abbr.orders.application.port.out.IdGenerator;
import java.util.UUID;

public class UuidGenerator implements IdGenerator {
    @Override
    public String newId() {
        return UUID.randomUUID().toString();
    }
}
