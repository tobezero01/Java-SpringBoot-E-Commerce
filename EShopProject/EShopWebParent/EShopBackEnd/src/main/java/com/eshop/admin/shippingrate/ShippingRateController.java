package com.eshop.admin.shippingrate;

import com.eshop.admin.exception.ShippingRateAlreadyExistsException;
import com.eshop.admin.exception.ShippingRateNotFoundException;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.paging.PagingAndSortingParam;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ShippingRateController {

    @Autowired private ShippingRateService shippingRateService;

    String defaultRedirect = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";

    @GetMapping("/shipping_rates")
    public String listFirstPage() {
        return defaultRedirect;
    }

    @GetMapping("/shipping_rates/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum ,
                             @PagingAndSortingParam(listName = "shippingRates") PagingAndSortingHelper helper
    ) {
        shippingRateService.listByPage(pageNum,helper );
        return "shipping_rates/shipping_rates";
    }

    @GetMapping("/shipping_rates/new")
    public String newRate(Model model) {
        List<Country> listCountries = shippingRateService.listAllCountries();
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("rate" , new ShippingRate());
        model.addAttribute("pageTitle" , "New Rate");

        return "shipping_rates/shipping_rate_form";
    }

    @PostMapping("shipping_rates/save")
    public String saveRate(ShippingRate rate , RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.save(rate);
            redirectAttributes.addFlashAttribute("message" , "The shipping rate has been saved successfully");
        } catch ( ShippingRateAlreadyExistsException exception) {
            redirectAttributes.addFlashAttribute("message" , exception.getMessage());
        }
        return defaultRedirect;
    }

    @GetMapping("/shipping_rates/edit/{id}")
    public String editRate(@PathVariable(name = "id") Integer id ,
                            Model model , RedirectAttributes redirectAttributes)  {
        try{
            ShippingRate rate = shippingRateService.get(id);
            List<Country> listCountries = shippingRateService.listAllCountries();

            model.addAttribute("rate" , rate);
            model.addAttribute("pageTitle" , "Edit the Shipping Rate (ID : " + id +" )");
            model.addAttribute("listCountries", listCountries);

            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return defaultRedirect;
        }

    }

    @GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
    public String updateCODSupport(@PathVariable(name = "id") Integer id ,
                                   @PathVariable(name = "supported") Boolean supported ,
                                   Model model , RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.updateCODSupport(id, supported);
            redirectAttributes.addFlashAttribute("message" , "COD support for shipping rate ID = " + id + " has been updated successfully!");
        } catch (ShippingRateNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message" , exception.getMessage());
        }
        return defaultRedirect;
    }

    @GetMapping("/shipping_rates/delete/{id}")
    public String deleteRate(@PathVariable(name = "id") Integer id ,
                              Model model , RedirectAttributes redirectAttributes) throws IOException {
        try{
            shippingRateService.delete(id);
            redirectAttributes.addFlashAttribute("message" , "The shipping rate ID = " + id + " has been deleted successfully!");
        } catch (ShippingRateNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }
        return defaultRedirect;
    }
}
