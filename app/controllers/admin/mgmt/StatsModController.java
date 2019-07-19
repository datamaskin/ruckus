package controllers.admin.mgmt;

import controllers.AbstractController;
import controllers.admin.AdminSecuredActionResponse;
import dao.DaoFactory;
import models.user.UserRole;
import play.mvc.Http;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;

/**
 * Created by mwalsh on 8/17/14.
 */
public class StatsModController extends AbstractController {

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result index(){
        return ok(views.html.admin.mgmt.statsMod.render());
    }
    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result statsModUpload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadedFilePart = body.getFile("importFile");
        if (uploadedFilePart != null) {
            File file = uploadedFilePart.getFile();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                line = reader.readLine();
                while(line != null){
                    String[] parts = line.split(",");
                    try{
                        int statsAthleteId = Integer.parseInt(parts[0]);
                        float projection = Float.parseFloat(parts[1]);
                        DaoFactory.getStatsDao().updateAllFutureAthleteProjections(statsAthleteId, projection, new Date());
                    } catch(Exception e){
                        //headers, who cares...
                    }
                    line = reader.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
                flash("error", e.getMessage());
            }
        } else {
            flash("error", "Missing file");
        }
        return redirect("/admin/mgmt/statsMod");
    }

}
