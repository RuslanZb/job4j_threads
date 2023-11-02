package ru.job4j.cash;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@ThreadSafe
public class AccountStorage {
    @GuardedBy("this")
    private final HashMap<Integer, Account> accounts = new HashMap<>();

    public synchronized boolean add(Account account) {
        return Objects.isNull(accounts.putIfAbsent(account.id(), account));
    }

    public synchronized boolean update(Account account) {
        boolean rsl = getById(account.id()).isPresent();
        if (rsl) {
            accounts.put(account.id(), account);
        }
        return rsl;
    }

    public synchronized void delete(int id) {
        accounts.remove(id);
    }

    public synchronized Optional<Account> getById(int id) {
        Optional<Account> rsl = Optional.empty();
        for (Account account : accounts.values()) {
            if (account.id() == id) {
                rsl = Optional.of(account);
            }
        }
        return rsl;
    }

    public boolean transfer(int fromId, int toId, int amount) {
        boolean rsl = false;
        Optional<Account> source = getById(fromId);
        Optional<Account> destination = getById(toId);
        if (source.isPresent() && destination.isPresent() && source.get().amount() >= amount) {
            update(new Account(destination.get().id(), destination.get().amount() + amount));
            update(new Account(source.get().id(), source.get().amount() - amount));
            rsl = true;
        }
        return rsl;
    }
}