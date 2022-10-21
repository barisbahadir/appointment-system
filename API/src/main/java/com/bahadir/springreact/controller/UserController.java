package com.bahadir.springreact.controller;

import com.bahadir.springreact.exception.ResourceNotFoundException;
import com.bahadir.springreact.model.User;
import com.bahadir.springreact.payload.*;
import com.bahadir.springreact.repository.PollRepository;
import com.bahadir.springreact.repository.UserRepository;
import com.bahadir.springreact.repository.VoteRepository;
import com.bahadir.springreact.security.CurrentUser;
import com.bahadir.springreact.security.UserPrincipal;
import com.bahadir.springreact.service.PollService;
import com.bahadir.springreact.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollService pollService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {

        return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailabilty checkUsernameAvailability(@RequestParam(value = "username") String username) {

        Boolean isAvailable = !userRepository.existsByUsername(username);

        return new UserIdentityAvailabilty(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailabilty checkEmailAvailability(@RequestParam(value = "email") String email) {

        Boolean isAvailable = !userRepository.existsByEmail(email);

        return new UserIdentityAvailabilty(isAvailable);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        long pollCount = pollRepository.countByCreatedBy(user.getId());
        long voteCount = voteRepository.countByUserId(user.getId());

        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreateAt(), pollCount, voteCount);

        return userProfile;
    }

    @GetMapping("/users/{username}/polls")
    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username") String username,
                                                         @CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        return pollService.getPollsCreatedBy(username, currentUser, page, size);
    }

    @GetMapping("/users/{username}/votes")
    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
                                                       @CurrentUser UserPrincipal currentUser,
                                                       @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        return pollService.getPollsVotedBy(username, currentUser, page, size);
    }
}

