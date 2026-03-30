package org.woojukang.remixlab.global.client.ai.dto.request.prompt.template;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InitPromptTemplate implements PromptTemplate{

    // 플롯 생성 프롬프트
    INIT_PLOT_MAKING("""
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
            
            Language Rules:
            
            The following fields MUST be written in Korean:
            - title
            - genre
            - mood
            - mainCharacter.name
            - mainCharacter.appearance
            - mainCharacter.personality
            - scenes[].sceneDescription
            
            All other fields MUST be written in English.
            
            IMPORTANT RULES (VERY STRICT):
            - Output MUST be valid JSON only. Do not include any explanation.
            - Use camelCase for ALL field names exactly as shown below.
            - Do NOT use snake_case.
            - visualElements MUST be a single string (comma-separated), NOT an array.
            - Do NOT add extra fields.
            - Do NOT omit any fields.
            - scenes MUST contain exactly 5 items.
            
            User Idea:
            {user_input}
            
            Output JSON format:
            
            {
              "title": "",
              "genre": "",
              "mood": "",
            
              "mainCharacter": {
                "name": "",
                "appearance": "",
                "personality": ""
              },
            
              "worldSetting": "",
            
              "scenes": [
                {
                  "sceneNumber": 1,
                  "sceneDescription": "",
                  "visualElements": "",
                  "cameraAngle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                },
                {
                  "sceneNumber": 2,
                  "sceneDescription": "",
                  "visualElements": "",
                  "cameraAngle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                },
                {
                  "sceneNumber": 3,
                  "sceneDescription": "",
                  "visualElements": "",
                  "cameraAngle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                },
                {
                  "sceneNumber": 4,
                  "sceneDescription": "",
                  "visualElements": "",
                  "cameraAngle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                },
                {
                  "sceneNumber": 5,
                  "sceneDescription": "",
                  "visualElements": "",
                  "cameraAngle": "",
                  "lighting": "",
                  "emotion": "",
                  "motion": ""
                }
              ]
            }
         
            """),

    // 이미지 생성을 위한 프롬프트 생성기
    INIT_IMAGE_PROMPT_MAKING("""
            You are an AI assistant that generates cinematic image prompts.
            
            You will receive a JSON object describing a story world and its scenes.
            
            INPUT JSON STRUCTURE:
            
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
            }
            
            INPUT JSON:
            {input_json}
            
            Your task:
            
            For each scene in the "scenes" array, generate a cinematic image prompt.
            
            The prompt should follow this structure:
            
            "A cinematic scene of {scene_description},
            featuring {mainCharacter.appearance},
            set in {world_setting},
            including {visual_elements},
            {camera_angle} shot,
            {lighting} lighting,
            {emotion} atmosphere,
            dynamic motion showing {motion},
            highly detailed, cinematic lighting, ultra realistic, film still"
            
            Rules:
            
            1. Generate exactly one prompt per scene.
            2. Maintain the same character appearance across scenes.
            3. Maintain the same world style across scenes.
            4. Convert visual_elements into natural descriptive phrases.
            5. Use the emotion field to influence the atmosphere of the scene.
            
            Return JSON only in the following format:
            
            {
              "imagePrompts": [
                {
                  "sceneNumber": number,
                  "prompt": string
                }
              ]
            }
            
            Do not return explanations.
            Return JSON only.
            """),
    INIT_VIDEO_PROMPT_MAKING(""" 
            You are a professional cinematic video director AI.
            
            You will be given a structured JSON input.
            
            Your task is to interpret the JSON and generate a single cohesive cinematic video.
            
            ---
            
            RULES:
            
            - Use all information inside the JSON as the source of truth
            - The JSON contains story, character, and scene data
            - Each scene must be used in order (sceneNumber)
            - Maintain visual and narrative consistency across the entire video
            - Ensure the main character remains visually identical across all scenes
            - Convert all scene descriptions into cinematic visual storytelling
            - Use appropriate camera angles, lighting, emotions, and motion from the data
            - Do not add new story elements that are not in the JSON
            - Do not output text, subtitles, or UI elements in the video
            - Focus on realism and cinematic quality
            
            ---
            
            INPUT_JSON:
            {{input_json}}
            
            ---
            
            OUTPUT:
            Generate a single continuous cinematic video based on the JSON.
            """);


    private final String template;


    @Override
    public String getTemplate(){
        return template;
    }


}
