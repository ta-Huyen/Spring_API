//package com.example.spring.events;
//
//import com.example.spring.entity.User;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class EventHandler {
//	private final SimpMessagingTemplate simpMessagingTemplate;
//
//	private final EntityLinks entityLinks;
//
//	public EventHandler(SimpMessagingTemplate simpMessagingTemplate, EntityLinks entityLinks) {
//		this.simpMessagingTemplate = simpMessagingTemplate;
//		this.entityLinks = entityLinks;
//	}
//
//
//	@EventListener
//	public void newUser(UserCreationEvent<User> savedUser) {
//		log.info("New User created Event");
//
//		this.simpMessagingTemplate.convertAndSend(
//				WebSocketConfig.MESSAGE_PREFIX + "/newUser", getPath(savedUser.getUser()));
//	}
//
//	@EventListener
//	public void deleteUser(UserDeletionEvent<User> deletedUser) {
//		log.info("User deleted Event");
//		this.simpMessagingTemplate.convertAndSend(
//				WebSocketConfig.MESSAGE_PREFIX + "/deleteUser",getPath(deletedUser.getUser()));
//	}
//
//	/**
//	 * Take an {@link user} and get the URI using Spring Data REST's {@link EntityLinks}.
//	 *
//	 * @param user
//	 */
//	private String getPath(User user) {
//		return this.entityLinks.linkForSingleResource(user.getClass(),
//				user.getId()).toUri().getPath();
//	}
//}
//
