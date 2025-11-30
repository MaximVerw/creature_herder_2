package io.github.creature.herder.screen;

import io.github.creature.herder.entity.customer.Customer;

import static io.github.creature.herder.screen.BuildingScreen.RANDOM;

public class World {

    public static final int MAX_CUSTOMERS = 5;

    public static void update(float delta){
        updateCustomers();
    }

    public static void updateCustomers(){
        if (BuildingScreen.customers.size()<MAX_CUSTOMERS && RANDOM.nextFloat()<1f && BuildingScreen.customers.stream().allMatch(c -> c.getPath().isEmpty())){
            BuildingScreen.customers.add(new Customer());
        }
    }
}
