package com.zoom.controllers;

import com.zoom.entity.Invite;
import com.zoom.entity.Meeting;
import com.zoom.entity.User;
import com.zoom.service.*;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/meeting-controller")
public class MeetingController {

    private final MeetingService meetingService;
    private final UserService userService;
    private final InviteService inviteService;
    private final EmailService emailService;

    @Autowired
    public MeetingController(MeetingService meetingService, UserService userService, InviteService inviteService, EmailService emailService) {
        this.meetingService = meetingService;
        this.userService = userService;
        this.inviteService = inviteService;
        this.emailService = emailService;
    }

    @GetMapping("/scheduleMeeting")
    public String scheduleMeeting(Model model) {
        Meeting meeting = new Meeting();
        model.addAttribute("meeting", meeting);
        return "schedule-meeting-form"; // Ensure you have this Thymeleaf template
    }

    @PostMapping("/scheduleMeeting")
    public String scheduleMeeting(@ModelAttribute Meeting meeting,
                                  Authentication authentication,
                                  @RequestParam("invitees") String invitees) {
        User currentLoggedInUser = userService.getCurrentUser();
        if (currentLoggedInUser == null) {
            return "redirect:/user/login";
        }

        List<String> inviteeEmails = Arrays.asList(invitees.split("\\s*,\\s*"));
        List<User> validInvitees = new ArrayList<>();
        for (String email : inviteeEmails) {
            User invitedUser = userService.getUserByEmail(email.trim());
            if (invitedUser != null) {
                validInvitees.add(invitedUser);
            }
        }

        meeting.setInvitedUsers(validInvitees);
        meeting.setHost(currentLoggedInUser);
        meetingService.createMeeting(meeting);

        // Send email notifications
        String[] invitesEmail = new String[validInvitees.size()];
        int i = 0;
        for (User temp : validInvitees) {
            invitesEmail[i++] = temp.getEmail();
        }
        String host=meeting.getHost().getEmail();
        String title=meeting.getTitle();
        int duration= meeting.getDuration();
        LocalDateTime dateTime= meeting.getDateTime();
        System.out.println("Host-> "+host+"Title-> "+title+"Duration-> "+duration+"DateTime-> "+dateTime);

        emailService.sendEmail(invitesEmail,title,host,dateTime,duration);
        return "redirect:/userDashboard";
    }

    @GetMapping("/startScheduledMeeting")
    public String startScheduledMeeting(@RequestParam("meetingId") Integer meetingId,
                                        Model model) {
        Meeting meeting = meetingService.findMeetingById(meetingId);
        return "redirect:/newMeeting"; // Adjust as needed for starting the meeting
    }

    @GetMapping("/deleteScheduledMeeting")
    public String deleteScheduledMeeting(@RequestParam("meetingId") Integer meetingId) {
        Meeting meeting = meetingService.findMeetingById(meetingId);
        meetingService.delete(meeting);
        return "redirect:/userDashboard";
    }

    @PostMapping("/acceptMeetingInvite")
    public String acceptMeetingInvite(@RequestParam("inviteId") Integer meetingId) {
        User currentLoggedInUser = userService.getCurrentUser();
        Integer userId = currentLoggedInUser.getId();
        Invite invite = inviteService.findByMeetingIdAndUserId(meetingId, userId);
        invite.setStatus(1);
        inviteService.saveInvite(invite);
        return "redirect:/userDashboard";
    }

    @PostMapping("/declineMeetingInvite")
    public String declineMeetingInvite(@RequestParam("inviteId") Integer meetingId) {
        User currentLoggedInUser = userService.getCurrentUser();
        Meeting meeting = meetingService.findMeetingById(meetingId);
        Integer userId = currentLoggedInUser.getId();
        currentLoggedInUser.getInvitedMeetings().remove(meeting);
        userService.updateUser(currentLoggedInUser);
        return "redirect:/userDashboard";
    }
}