package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

//@RestController
//@RequestMapping("/{path}")
@Validated
abstract class Controller<E, T> {
    HashMap<E, T> map = new HashMap<>();
    int id=1;
    String path;

    @GetMapping("/{path}")
    public Map<E, T> getAll() {
        return map;
    }

    @PostMapping("/{path}")
    public abstract T create(@Valid @RequestBody T t);

    @PutMapping("/{path}")
   public abstract T update(@Valid @RequestBody T t);


    int generateId() {//метод генерации id
        return id++;
    }

}
