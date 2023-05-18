package kz.kenzhakhimov.cirkuitbraker.controllers;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import kz.kenzhakhimov.cirkuitbraker.dto.ItemDto;
import kz.kenzhakhimov.cirkuitbraker.managers.ItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import java.util.List;
import java.util.function.Supplier;

@Controller
public class ItemStoreService
{

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemStoreService.class);

    @Autowired
    public ItemManager itemManager;

    @Autowired
    private CircuitBreaker countCircuitBreaker;

    @RequestMapping(value = "/home", method= RequestMethod.GET)
    public String home(HttpServletRequest request, Model model)
    {
        return "home";
    }

    @RequestMapping(value = "/items", method=RequestMethod.GET)
    public String items(HttpServletRequest request, Model model)
    {
        Supplier<List<ItemDto>> booksSupplier =
                countCircuitBreaker.decorateSupplier(() -> itemManager.getAllItemsFromLibrary());

        LOGGER.info("Going to start calling the REST service with Circuit Breaker");
        List<ItemDto> items = null;
        for(int i = 0; i < 15; i++)
        {
            try
            {
                LOGGER.info("Retrieving books from returned supplier");
                items = booksSupplier.get();
            }
            catch(Exception e)
            {
                LOGGER.error("Could not retrieve books from supplier", e);
            }
        }
        model.addAttribute("items", items);

        return "items";
    }
}
