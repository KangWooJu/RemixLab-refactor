package org.woojukang.remixlab.global.client.ai.dto.request.prompt.template;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DirectPromptTemplate implements PromptTemplate {

    DIRECT_PLOT_FROM_PROMPT("""
             You are a professional cinematic story planner for AI-generated media.
            
            Convert the user's idea into a structured story that will later be used
            for image and video generation.
            
            Requirements:
            - Focus on visual storytelling
            - Maintain consistent character design
            - The story MUST contain exactly 5 scenes
            - Each scene must be visually descriptive
            - Each scene must include camera, lighting, emotion, and motion
            - Keep scenes concise but cinematic
            
            User Idea:
            {user_input}
            
            Output JSON format:
            
            {
              "title": "",
              "genre": "",
              "mood": "",
            
              "main_character": {
                "name": "",
                "appearance": "",
                "personality": ""
              },
            
              "world_setting": "",
            
              "scenes": [
                {
                  "scene_number": 1,
                  "scene_description": "",
                  "visual_elements": [],
                  "camera_angle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                },
                {
                  "scene_number": 2,
                  "scene_description": "",
                  "visual_elements": [],
                  "camera_angle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                },
                {
                  "scene_number": 3,
                  "scene_description": "",
                  "visual_elements": [],
                  "camera_angle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                },
                {
                  "scene_number": 4,
                  "scene_description": "",
                  "visual_elements": [],
                  "camera_angle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                },
                {
                  "scene_number": 5,
                  "scene_description": "",
                  "visual_elements": [],
                  "camera_angle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                }
              ]
            }"""),
    DIRECT_IMAGE_FROM_PROMPT("""
            You are an AI cinematic image generation system.
            
            Generate multiple cinematic images based on the user's prompt.
            
            User Prompt:
            {user_input}
            
            Your job is to:
            1. Interpret the user's idea
            2. Create five visually distinct cinematic scenes based on the prompt
            3. Generate one image for each scene
            4. Return the images encoded in base64
            
            Image style requirements:
            - cinematic composition
            - highly detailed environment
            - realistic lighting
            - consistent visual style
            - ultra realistic
           
            
            Important rules:
            - Generate exactly FIVE images
            - Each image should represent a different cinematic moment or visual variation
            - Maintain visual consistency across the images
            
            Return JSON only:
            
            {
              "images_base64": [
                "base64_image_1",
                "base64_image_2",
                "base64_image_3",
                "base64_image_4",
                "base64_image_5"
              ]
            }"""),
    DIRECT_VIDEO_FROM_PROMPT("""
            You are an AI cinematic video generation system.
            
            Generate a short cinematic video directly based on the user's prompt.
            
            User Prompt:
            {user_input}
            
            Your job is to:
            1. Interpret the user's idea
            2. Imagine the visual scenes that fit the prompt
            3. Generate a short cinematic video representing the prompt
            
            Video style requirements:
            - cinematic composition
            - realistic lighting
            - smooth camera movement
            - visually coherent environment
            - film style pacing
            
            Video specifications:
            - resolution: 1920x1080
            - aspect ratio: 16:9
            - duration: 5 to 10 seconds
            - cinematic quality
            
            Important rules:
            - Generate exactly ONE video
            - Focus on visual storytelling
            - Maintain visual consistency
            
            Return JSON only:
            
            {
              "video_url": "GENERATED_VIDEO_URL"
            }""");

    private final String template;


    @Override
    public String getTemplate(){
        return template;
    }
}
