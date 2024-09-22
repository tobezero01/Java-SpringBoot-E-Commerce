package com.eshop.admin.setting;

import com.eshop.admin.FileUploadUtil;
import com.eshop.common.entity.Currency;
import com.eshop.common.entity.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class SettingController {
    @Autowired
    private SettingService settingService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/settings")
    public String listAll(Model model) {
        List<Setting> listSettings = settingService.listAllSettings();
        List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("listCurrencies", listCurrencies);
        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }
        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage")MultipartFile multipartFile,
                                      HttpServletRequest request, RedirectAttributes re) throws IOException {
        GeneralSettingBag settingBag = settingService.getGeneralSettings();
        saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);
        updateSettingValueFromForm(request , settingBag.list());
        re.addFlashAttribute("message", "General settings have been saved");
        return  "redirect:/settings";
    }

    private void saveSiteLogo(MultipartFile multipartFile , GeneralSettingBag settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);
            String uploadDir = "../site-logo/";
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);
        if (findByIdResult.isPresent()) {
            Currency currency = findByIdResult.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    private void updateSettingValueFromForm(HttpServletRequest request, List<Setting> listSettings) {
        for (Setting setting : listSettings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }
        settingService.saveAll(listSettings);
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request , RedirectAttributes redirectAttributes) {
        List<Setting> mailServerSettings = settingService.getMailServerSettings();
        updateSettingValueFromForm(request, mailServerSettings);
        redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved");
        return "redirect:/settings";
    }

    @PostMapping("/settings/save_mail_templates")
    public String saveMailTemplatesSettings(HttpServletRequest request , RedirectAttributes redirectAttributes) {
        List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
        updateSettingValueFromForm(request, mailTemplateSettings);
        redirectAttributes.addFlashAttribute("message", "Mail templates settings have been saved");
        return "redirect:/settings";
    }
}
