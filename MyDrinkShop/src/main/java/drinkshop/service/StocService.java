package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StocService {

    private final Repository<Integer, Stoc> stocRepo;

    public StocService(Repository<Integer, Stoc> stocRepo) {
        this.stocRepo = stocRepo;
    }

    public List<Stoc> getAll() {
        return stocRepo.findAll();
    }

    public void add(Stoc s) {
        stocRepo.save(s);
    }

    public void update(Stoc s) {
        stocRepo.update(s);
    }

    public void delete(int id) {
        stocRepo.delete(id);
    }

    public boolean areSuficient(Reteta reteta) {
        List<IngredientReteta> ingredienteNecesare = reteta.getIngrediente();

        for (IngredientReteta e : ingredienteNecesare) {
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();

            double disponibil = stocRepo.findAll().stream()
                    .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                    .mapToDouble(Stoc::getCantitate)
                    .sum();

            if (disponibil < necesar) {
                return false;
            }
        }
        return true;
    }

    public void consuma(Reteta reteta) {
        // NODE 1
        if (!areSuficient(reteta)) {
            // NODE 2
            throw new IllegalStateException("Stoc insuficient pentru rețeta.");
        }

        // NODE 3
        for (IngredientReteta e : reteta.getIngrediente()) {
            // NODE 4
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();
            List<Stoc> ingredienteStoc = new ArrayList<>();
            for (Stoc stoc : stocRepo.findAll()) {
                if (stoc.getIngredient().equalsIgnoreCase(ingredient)) {
                    ingredienteStoc.add(stoc);
                }
            }
            double ramas = necesar;

            // NODE 5
            for (Stoc s : ingredienteStoc) {
                // NODE 6
                if (ramas <= 0)
                    // NODE 7
                    break;

                // NODE 8
                double deScazut = Math.min(s.getCantitate(), ramas);
                s.setCantitate((int)(s.getCantitate() - deScazut));
                ramas -= deScazut;
                stocRepo.update(s);
            }
        }
    } // NODE 9
}