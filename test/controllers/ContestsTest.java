package controllers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dan on 4/11/14.
 */
public class ContestsTest {
//    @Test
//    public void renderIndexWithNoContests_NotLoggedIn() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                Result result = callAction(controllers.routes.ref.Contests.index(), fakeRequest());
//
//                assertThat(status(result)).isEqualTo(OK);
//                assertThat("text/html").isEqualTo(contentType(result));
//
//                String content = contentAsString(result);
//
//                int ulEnd = content.indexOf("</ul>");
//                int ulBeginning = content.indexOf("<ul id=\"contests\">");
//                String liElements = content.substring(ulBeginning + "<ul id=\"contests\">".length(), ulEnd).trim();
//
//                assertThat(liElements).contains("");
//            }
//        });
//    }

//    @Test
//    public void renderIndexWith1Contest_NotLoggedIn() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                UserAttributes attributes = new UserAttributes("Dan MacLean", "dan@ruckusgaming.com");
//                attributes.save();
//
//                User user = new User("dan", "password", attributes);
//                user.save();
//
//                Contest newContest = new Contest(user, Contest.CONTEST_TYPE_H2H, Contest.CONTEST_SPORT_NFL, 5, 10, false, "1st - $1.80", 1);
//                newContest.save();
//
//                Result result = callAction(controllers.routes.ref.Contests.index(), fakeRequest());
//
//                assertThat(status(result)).isEqualTo(OK);
//                assertThat("text/html").isEqualTo(contentType(result));
//
//                String content = contentAsString(result);
//
//                int ulEnd = content.indexOf("</ul>");
//                int ulBeginning = content.indexOf("<ul id=\"contests\">");
//                String liElements = content.substring(ulBeginning + "<ul id=\"contests\">".length(), ulEnd).trim();
//
//                assertThat(liElements).contains("<li id=\"1\">$1</li>");
//            }
//        });
//    }

//    @Test
//    public void renderIndexWithNoContests_LoggedIn() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                Result result = callAction(controllers.routes.ref.Contests.index(), fakeRequest().withSession("user", "dmaclean"));
//
//                assertThat(status(result)).isEqualTo(OK);
//                assertThat("text/html").isEqualTo(contentType(result));
//
//                String content = contentAsString(result);
//
//                int ulEnd = content.indexOf("</ul>");
//                int ulBeginning = content.indexOf("<ul id=\"contests\">");
//                String liElements = content.substring(ulBeginning + "<ul id=\"contests\">".length(), ulEnd).trim();
//
//                assertThat(liElements).contains("");
//            }
//        });
//    }

//    @Test
//    public void renderIndexWith1Contest_LoggedIn() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                UserAttributes attributes = new UserAttributes("Dan MacLean", "dan@ruckusgaming.com");
//                attributes.save();
//
//                User user = new User("dan", "password", attributes);
//                user.save();
//
//                Contest newContest = new Contest(user, Contest.CONTEST_TYPE_H2H, Contest.CONTEST_SPORT_NFL, 5, 10, false, "1st - $1.80", 1);
//                newContest.save();
//
//                Result result = callAction(controllers.routes.ref.Contests.index(), fakeRequest().withSession("user", "dmaclean"));
//
//                assertThat(status(result)).isEqualTo(OK);
//                assertThat("text/html").isEqualTo(contentType(result));
//
//                String content = contentAsString(result);
//
//                int ulEnd = content.indexOf("</ul>");
//                int ulBeginning = content.indexOf("<ul id=\"contests\">");
//                String liElements = content.substring(ulBeginning + "<ul id=\"contests\">".length(), ulEnd).trim();
//
//                assertThat(liElements).contains("<li id=\"1\">$1</li>");
//            }
//        });
//    }

//    @Test
//    public void renderListWithNoContests() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                Result result = callAction(routes.ref.Contests.list(), fakeRequest());
//
//                assertThat(status(result)).isEqualTo(OK);
//                assertThat("application/json").isEqualTo(contentType(result));
//
//                String content = contentAsString(result);
//                ObjectMapper mapper = new ObjectMapper();
//
//                try {
//                    ArrayList<Contest> emptyContestList = new ArrayList<>();
//
//                    String expected = mapper.writeValueAsString(emptyContestList);
//                    assertThat(content).isEqualTo(expected);
//                } catch (JsonProcessingException e) {
//                    fail(e.getMessage());
//                }
//            }
//        });
//    }

//    @Test
//    public void renderListWithOneContests() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                /*
//                 * Create contest.
//                 */
//                UserAttributes attributes = new UserAttributes("Dan MacLean", "dan@ruckusgaming.com");
//                attributes.save();
//
//                User user = new User("dan", "password", attributes);
//                user.save();
//
//                String payout = "1st - $1.80";
//
//                Contest newContest = new Contest(user, Contest.CONTEST_TYPE_H2H, Contest.CONTEST_SPORT_NFL, 5, 10, false, payout, 1);
//                newContest.save();
//
//                Result result = callAction(routes.ref.Contests.list(), fakeRequest());
//
//                assertThat(status(result)).isEqualTo(OK);
//                assertThat("application/json").isEqualTo(contentType(result));
//
//                String content = contentAsString(result);
//                ObjectMapper mapper = new ObjectMapper();
//
//                try {
////                    ArrayList<Contest> contestList = new ArrayList<Contest>();
////                    contestList.add(newContest);
////
////                    String expected = mapper.writeValueAsString(contestList);
////                    assertThat(content).isEqualTo(expected);
////                    List l = mapper.readValue(content, ArrayList.class);
//                    System.out.println(content);
//                    JsonNode node = mapper.reader().readTree(content);
//
//                    assertThat(node.isArray());
//
//                    assertThat(node.fields().hasNext());
//
//                    JsonNode c = node.get(0);
//
////                    assertThat(l.size()).isEqualTo(1);
////
////                    Contest c = (Contest) l.get(0);
////
//
//                    assertThat(c.get("type").asText()).isEqualTo(Contest.CONTEST_TYPE_H2H);
////                    assertThat(c.get("sport").asText()).isEqualTo(Contest.CONTEST_SPORT_NFL);
//
////                    assertThat(c.sport).isEqualTo(Contest.CONTEST_SPORT_NFL);
////                    assertThat(c.size).isEqualTo(5);
////                    assertThat(c.capacity).isEqualTo(10);
////                    assertThat(c.isPublic).isEqualTo(false);
////                    assertThat(c.payout).isEqualTo(payout);
////                    assertThat(c.buyin).isEqualTo(1);
//                } catch (Exception e) {
//                    fail(e.getMessage());
//                }
//            }
//        });
//    }

//    @Test
//    public void renderCreate_NotLoggedIn() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                Result result = callAction(controllers.routes.ref.Contests.create(), fakeRequest());
//
//                assertThat(status(result)).isEqualTo(SEE_OTHER);
//                assertThat(redirectLocation(result)).isEqualTo("/contests");
//            }
//        });
//    }

//    @Test
//    public void renderCreate_LoggedIn() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                Result result = callAction(controllers.routes.ref.Contests.create(), fakeRequest().withSession("user", "dan"));
//
//                assertThat(status(result)).isEqualTo(OK);
//                assertThat("text/html").isEqualTo(contentType(result));
//            }
//        });
//    }

//    @Test
//    public void renderSave_NotLoggedIn() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                Result result = callAction(controllers.routes.ref.Contests.save(), fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
//                        "type", Contest.CONTEST_TYPE_H2H,
//                        "sport", Contest.CONTEST_SPORT_NFL,
//                        "capacity", "2",
//                        "buyin", "1"
//                )));
//
//                assertThat(status(result)).isEqualTo(SEE_OTHER);
//                assertThat(redirectLocation(result)).isEqualTo("/contests");
//            }
//        });
//    }

//    @Test
//    public void renderSave_LoggedIn() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                UserAttributes attributes = new UserAttributes("Dan MacLean", "dan@ruckusgaming.com");
//                attributes.save();
//
//                User user = new User("dan", "password", attributes);
//                user.save();
//
//                Result result = callAction(controllers.routes.ref.Contests.save(), fakeRequest().withSession("user", String.valueOf(user.id)).withFormUrlEncodedBody(ImmutableMap.of(
//                        "sport", Contest.CONTEST_SPORT_NFL,
//                        "capacity", "2",
//                        "buyin", "1"
//                )));
//
//                assertThat(status(result)).isEqualTo(SEE_OTHER);
//                assertThat(redirectLocation(result)).isEqualTo("/contests");
//
//                String contents = contentAsString(result);
//                assertThat(contents.contains("Your Head-to-Head NFL contest was successfully created."));
//
//                List<Contest> contests = Contest.find.all();
//                assertThat(contests.size()).isEqualTo(1);
//            }
//        });
//    }

//    @Test
//    public void renderSave_LoggedIn_PublicContest() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                UserAttributes attributes = new UserAttributes("Dan MacLean", "dan@ruckusgaming.com");
//                attributes.save();
//
//                User user = new User("dan", "password", attributes);
//                user.save();
//
//                Result result = callAction(controllers.routes.ref.Contests.save(), fakeRequest().withSession("user", String.valueOf(user.id)).withFormUrlEncodedBody(ImmutableMap.of(
//                        "sport", Contest.CONTEST_SPORT_NFL,
//                        "capacity", "2",
//                        "buyin", "1",
//                        "isPublic", "true"
//                )));
//
//                assertThat(status(result)).isEqualTo(BAD_REQUEST);
//
//                String contents = contentAsString(result);
//                assertThat(contents.contains("You may only create private Head-to-Head contests."));
//
//                List<Contest> contests = Contest.find.all();
//                assertThat(contests.size()).isEqualTo(0);
//            }
//        });
//    }

//    @Test
//    public void renderSave_LoggedIn_NonH2HContest() {
//        running(fakeApplication(inMemoryDatabase()), new Runnable() {
//            @Override
//            public void run() {
//                UserAttributes attributes = new UserAttributes("Dan MacLean", "dan@ruckusgaming.com");
//                attributes.save();
//
//                User user = new User("dan", "password", attributes);
//                user.save();
//
//                Result result = callAction(controllers.routes.ref.Contests.save(), fakeRequest().withSession("user", String.valueOf(user.id)).withFormUrlEncodedBody(ImmutableMap.of(
//                        "sport", Contest.CONTEST_SPORT_NFL,
//                        "capacity", "2",
//                        "buyin", "1",
//                        "type", Contest.CONTEST_TYPE_GPP
//                )));
//
//                assertThat(status(result)).isEqualTo(BAD_REQUEST);
//
//                String contents = contentAsString(result);
//                assertThat(contents.contains("You may only create private Head-to-Head contests."));
//
//                List<Contest> contests = Contest.find.all();
//                assertThat(contests.size()).isEqualTo(0);
//            }
//        });
//    }
}
