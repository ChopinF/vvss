package drinkshop.service.validator;

import drinkshop.domain.Product;

public class ProductValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {
        String errors = "";

        if (product.getId() <= 0)
            errors += "ID invalid!\n";

        // BVA/ECP Nume: lungimea trebuie sa fie intre 1 si 255
        if (product.getNume() == null || product.getNume().isEmpty())
            errors += "Numele nu poate fi gol!\n";
        else if (product.getNume().length() > 255)
            errors += "Numele este prea lung!\n";

        // BVA/ECP Pret: trebuie sa fie strict > 0 si <= 10000
        if (product.getPret() <= 0 || product.getPret() > 10000)
            errors += "Pret invalid!\n";

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
