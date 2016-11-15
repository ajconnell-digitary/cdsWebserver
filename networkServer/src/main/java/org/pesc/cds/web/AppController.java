package org.pesc.cds.web;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.repository.TransactionService;
import org.pesc.cds.service.OrganizationService;
import org.pesc.cds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@Controller
public class AppController {

    private static final Log log = LogFactory.getLog(AppController.class);

    @Value("${directory.server.base.url}")
    private String directoryServer;

    @Value("${networkServer.id}")
    private String localServerId;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;



    public static boolean hasRole(Collection<GrantedAuthority> authorities, String role) {
        boolean hasRole = false;
        for (GrantedAuthority authority : authorities) {
            hasRole = authority.getAuthority().equals(role);
            if (hasRole) {
                break;
            }
        }
        return hasRole;
    }


    private boolean buildCommonModel(Model model) {
        model.addAttribute("directoryServer", directoryServer);

        boolean isAuthenticated = false;


        //Check if the user is autenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

            User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Collection<GrantedAuthority> authorities = auth.getAuthorities();
            isAuthenticated = true;

            model.addAttribute("hasSupportRole", hasRole(authorities, "ROLE_SUPPORT"));
            model.addAttribute("hasAdminRole", hasRole(authorities, "ADMIN"));

            // model.addAttribute("roles", roleRepo.findAll() );
        }
        else {
            model.addAttribute("hasSupportRole", false);
            model.addAttribute("hasAdminRole", false);
        }

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("roles", userService.getRoles() );


        if (isAuthenticated) {
            org.pesc.cds.model.User activeUser = new org.pesc.cds.model.User();
            activeUser.setName("Admin");

            model.addAttribute("activeUser", activeUser);
        }
        return isAuthenticated;

    }


    @Autowired
    private TransactionService transactionService;


    private void setContentType(HttpServletResponse response, String fileFormat) {
        if (fileFormat.equalsIgnoreCase("pdf")) {
            response.setContentType("application/pdf");
        }
        else if (fileFormat.equalsIgnoreCase("text")) {
            response.setContentType("text/plain");
        }
        else if (fileFormat.equalsIgnoreCase("xml")) {
            response.setContentType("text/xml");
        }
        else if (fileFormat.equalsIgnoreCase("pescxml")) {
            response.setContentType("text/xml");
        }
        else if (fileFormat.equalsIgnoreCase("image")) {
            response.setContentType("image/png"); //TODO: how to get actual MIME type ???
        }
        else if (fileFormat.equalsIgnoreCase("edi")) {
            response.setContentType("application/edi-x12"); //TODO: could be application/edifact ???
        }

    }


    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public void getFile(
            @RequestParam("tran_id") Integer tranID,
            @RequestParam(value = "show_request", required = false) Boolean showRequest,
            HttpServletResponse response) {
        try {

            //TODO for security: create randomize the transaction ID with a lookup table.

            Transaction transaction = transactionService.findById(tranID);

            if (transaction == null) {
                throw new RuntimeException("Invalid transaction id.");
            }

            String filePath = BooleanUtils.isTrue(showRequest)?transaction.getRequestFilePath():transaction.getFilePath();
            String fileFormat = BooleanUtils.isTrue(showRequest)?"xml":transaction.getFileFormat();

            InputStream is = new FileInputStream(new File(filePath));

            setContentType(response, fileFormat);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("Error writing file to output stream. Transaction id " + tranID, e);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
    @RequestMapping("/admin")
    public String getAdminPage(Model model) {

        buildCommonModel(model);

        return "home";
    }

    @RequestMapping("/upload-status")
    public String getUploadStatus(Model model) {

        buildCommonModel(model);

        return "fragments :: upload-status";
    }

    @RequestMapping("/transaction-report")
    public String getTransactionsPage(Model model) {

        buildCommonModel(model);

        return "fragments :: transactions";
    }


    @RequestMapping("/users")
    public String getUsersPage(Model model) {

        buildCommonModel(model);

        return "fragments :: users";
    }

    @RequestMapping({"/user-details"})
    public String getUserDetails(HttpServletRequest request, Model model) {


        buildCommonModel(model);

        return "fragments :: user-details";
    }


    @RequestMapping("/upload")
    public String getTransfersPage(Model model) {

        buildCommonModel(model);
        //If Institution, use OrgId.  Otherwise need to ask and look up Source Institution Info.
        //OrgId known, Source Institution (if applicable) not known.
        JSONObject organization = organizationService.getOrganization(Integer.valueOf(localServerId));
        boolean institution = organizationService.isInstitution(organization);
        model.addAttribute("institution", institution);
        return "fragments :: upload";
    }

    @RequestMapping({ "/", "/home" })
    public String viewHome(Model model) {

        buildCommonModel(model);
        return "home";
    }

    @RequestMapping("/about")
    public String getAboutPage(Model model) {
        buildCommonModel(model);


        return "fragments :: about";
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
